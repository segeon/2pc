/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-02 16:20 创建
 *
 */
package com.thh.tpc.cbrcserver.domain;

import com.thh.tpc.protocol.conf.TransactionManagerNode;
import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.URL;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
public class CbrcParticipant extends AbstractParticipant {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private TransactionManagerNode transactionManagerNode;

    public CbrcParticipant(String name, URL address) {
        super(name, address);
    }

    @Override
    public VoteResult vote(final DistributedTransaction transaction) {
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    getParticipantLogKeeper().saveOrUpdate(transaction);
                    getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Yes);
                }
            });
            return new VoteResultImpl(transaction.id(), name(), System.currentTimeMillis(), Vote.Yes, "OK");
        } catch (Exception e) {
            logger.error("{} votes No for {}", name(), transaction.id(), e);
            try {
                getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Abort);
            } catch (Exception e2) {
                logger.error("Fail to record Abort status for transaction {}", transaction.id(), e2);
            }
            return new VoteResultImpl(transaction.id(), name(), System.currentTimeMillis(), Vote.No, e.getMessage());
        }
    }

    @Override
    public void commit(final DistributedTransaction transaction) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Commit);
            }
        });
    }

    @Override
    public void abort(final DistributedTransaction transaction) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Abort);
            }
        });
    }

    @Override
    public Decision resolveDecisionByCTP(DistributedTransaction transaction) {
        final List<Node> participants = transaction.participants();
        final DecisionQueryMessage decisionQueryMessage = new DecisionQueryMessage();
        decisionQueryMessage.setTransactionId(transaction.id());
        for (Node participant : participants) {
            final DecisionQueryResult result = messageSender.sendDecisionQuery2Others(participant, decisionQueryMessage);
            if (!result.getDecision().equals(Decision.Unknown)) {
                return result.getDecision();
            }
        }
        final DecisionQueryResult result = messageSender.sendDecisionQuery2Others(transactionManagerNode, decisionQueryMessage);
        return result.getDecision();
    }
}
