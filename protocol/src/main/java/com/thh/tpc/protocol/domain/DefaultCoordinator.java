/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 21:35 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.thh.tpc.protocol.service.AbstractLogKeeper;
import com.thh.tpc.protocol.service.MessageSender;
import com.thh.tpc.protocol.util.Constants;
import com.thh.tpc.protocol.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author daidai@yiji.com
 */

public class DefaultCoordinator implements Coordinator, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String name;
    private List<Node> participants;
    private Map<String, Node> participantMap;
    private URL address;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private AbstractLogKeeper abstractLogKeeper;

    public DefaultCoordinator(String name, List<Node> participants, URL address) {
        this.name = name;
        this.participants = participants;
        this.participantMap = new HashMap<>();
        for (Node participant : participants) {
            participantMap.put(participant.name(), participant);
        }
        this.address = address;
        executorService = Executors.newFixedThreadPool(10, new NamedThreadFactory("CoordinatorSendingThreads", false));
        scheduledExecutorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("CoordinatorRecoveringThread"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        recover();
    }

    public Decision handleTransaction(DistributedTransaction transaction) {
        abstractLogKeeper.saveOrUpdate(transaction);
        sendVoteRequest(transaction);
        final Decision decision = makeDecision(transaction);
        abstractLogKeeper.saveOrUpdate(transaction);
        if (decision.equals(Decision.Abort)) {
            abstractLogKeeper.recordMsg(transaction, TransactionStatus.Abort);
        } else if (decision.equals(Decision.Commit)) {
            abstractLogKeeper.recordMsg(transaction, TransactionStatus.Commit);
        } else {
            //should never reach here!
            logger.error("Invalid decision {} for transaction {}", decision, transaction.id());
        }
        notifyDecision(transaction, decision);
        return decision;
    }

    public List<Node> participants() {
        return participants;
    }


    public void sendVoteRequest(final DistributedTransaction transaction) {
        final List<? extends Node> participants = transaction.participants();
        final CountDownLatch countDownLatch = new CountDownLatch(participants.size());
        participants.forEach(node -> executorService.submit(new Runnable() {
            public void run() {
                final VoteResult voteResult = messageSender.sendVoteRequest2Participant(node, transaction);
                transaction.voteResults().add(voteResult);
                countDownLatch.countDown();
            }
        }));
        try {
            abstractLogKeeper.recordMsg(transaction, TransactionStatus.Start2PC);
            countDownLatch.await();
            printVotes(transaction);
        } catch (InterruptedException e) {
        }
    }

    private void printVotes(DistributedTransaction transaction) {
        StringBuilder builder = new StringBuilder();
        builder.append("vote results for ").append(transaction.id()).append("\n");
        for (VoteResult voteResult : transaction.voteResults()) {
            builder.append(voteResult.voterName()).append(" voted ").append(voteResult.result().name())
                    .append(", message = ").append(voteResult.getDescription()).append("\n");
        }
        logger.info("{}", builder.toString());
    }


    public Decision makeDecision(DistributedTransaction transaction) {
        Decision decision = Decision.Commit;
        for (VoteResult voteResult : transaction.voteResults()) {
            if (voteResult.result().equals(Vote.No) || voteResult.result().equals(Vote.Unknow)) {
                decision = Decision.Abort;
                break;
            }
        }
        return decision;
    }

    public void notifyDecision(DistributedTransaction transaction, Decision decision) {
        final DecisionMessage decisionMessage = new DecisionMessage(transaction.id(), decision);
        if (decision.equals(Decision.Commit)) {
            for (Node node : transaction.participants()) {
                messageSender.sendDecision2Participant(node, decisionMessage);
            }
        } else {
            for (VoteResult result : transaction.voteResults()) {
                if (result.result().equals(Vote.Yes)) {
                    final Node node = participantMap.get(result.voterName());
                    if (node == null) {
                        throw new IllegalStateException("没有找到名为" + result.voterName() + "的参与者地址信息!");
                    }
                    messageSender.sendDecision2Participant(node, decisionMessage);
                }
            }
        }
    }

    public void recover() {
        try {
            logger.info("start to recover undecided transactions ...");
            final List<? extends LogRecord> undecidedTransactions = abstractLogKeeper.findUndecidedTransactions();
            final LinkedList<String> transactionIds = new LinkedList<>();
            undecidedTransactions.forEach(transaction -> transactionIds.add(transaction.transactionId()));
            final List<? extends DistributedTransaction> transactionList = abstractLogKeeper.findTranactionByIds(transactionIds);
            for (DistributedTransaction transaction : transactionList) {
                abstractLogKeeper.recordMsg(transaction, TransactionStatus.Abort);
            }
            logger.info("recovered {} undecided transactions", transactionList.size());
        } catch (Exception e) {
            logger.info("recover undecided transactions exception: ", e);
        }
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                recover();
            }
        }, Constants.RECOVER_INTERVAL_SECS, TimeUnit.SECONDS);
    }

    public String name() {
        return name;
    }

    public URL communicationAddr() {
        return address;
    }
}
