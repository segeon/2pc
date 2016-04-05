/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 14:29 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.thh.tpc.protocol.service.AbstractLogKeeper;
import com.thh.tpc.protocol.util.Constants;
import com.thh.tpc.protocol.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author daidai@yiji.com
 */

public abstract class AbstractParticipant implements Participant, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String name;
    private URL address;
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private AbstractLogKeeper participantLogKeeper;


    public AbstractParticipant(String name, URL address) {
        this.name = name;
        this.address = address;
        scheduledExecutorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("ParticipantRecoverThread"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        recover();
    }

    public AbstractLogKeeper getParticipantLogKeeper() {
        return participantLogKeeper;
    }

    public void setParticipantLogKeeper(AbstractLogKeeper participantLogKeeper) {
        this.participantLogKeeper = participantLogKeeper;
    }

    @Override
    public abstract VoteResult vote(DistributedTransaction transaction);

    @Override
    public abstract void commit(DistributedTransaction transaction);

    @Override
    public abstract void abort(DistributedTransaction transaction);

    @Override
    public void recover() {
        try {
            logger.info("开始恢复未完成的事务...");
            final List<LogRecord> undecidedLogRecords = participantLogKeeper.findUndecidedTransactions();
            final LinkedList<String> uncertainIds = new LinkedList<>();
            for (LogRecord logRecord : undecidedLogRecords) {
                if (logRecord.status().equals(TransactionStatus.No)) {
                    logRecord.setStatus(TransactionStatus.Abort);
                    participantLogKeeper.saveOrUpdate(logRecord);
                } else {
                    uncertainIds.add(logRecord.transactionId());
                }
            }

            if (!uncertainIds.isEmpty()) {
                final List<DistributedTransaction> transactionList = participantLogKeeper.findTranactionByIds(uncertainIds);
                for (DistributedTransaction transaction : transactionList) {
                    final Decision vote = resolveDecisionByCTP(transaction);
                    if (vote.equals(Decision.Abort)) {
                        abort(transaction);
                    } else if (vote.equals(Decision.Commit)) {
                        commit(transaction);
                    } else {
                        continue;
                    }
                }
            }
            logger.info("此次共恢复{}个事务", uncertainIds.size());
        } catch (Exception e) {
            logger.error("恢复事务出错: ", e);
        }
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                recover();
            }
        }, Constants.RECOVER_INTERVAL_SECS, TimeUnit.SECONDS);
    }

    @Override
    public abstract Decision resolveDecisionByCTP(DistributedTransaction transaction);

    @Override
    public String name() {
        return name;
    }

    @Override
    public URL communicationAddr() {
        return address;
    }
}
