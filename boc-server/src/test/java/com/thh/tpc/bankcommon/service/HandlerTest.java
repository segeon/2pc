/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-02 13:51 创建
 *
 */
package com.thh.tpc.bankcommon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.thh.tpc.protocol.service.LogRecordRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.thh.tpc.TestBase;
import com.thh.tpc.bankcommon.domain.Account;
import com.thh.tpc.protocol.domain.LogRecordEntity;
import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.MessageSender;
import com.thh.tpc.protocol.util.TestUtil;

/**
 * @author daidai@yiji.com
 */
public class HandlerTest extends TestBase {
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LogRecordRepository logRecordRepository;

    @Test
    public void testHandler() {
        final Account one = accountRepository.findOne(1);
        final long balance = one.getBalance();
        final DistributedTransactionImpl transaction = TestUtil.getTransaction();
        final VoteResult voteResult = messageSender.sendVoteRequest2Participant(TestUtil.getBocNode(), transaction);
        assertThat(voteResult.result()).isEqualTo(Vote.Yes);

        messageSender.sendDecision2Participant(TestUtil.getBocNode(), new DecisionMessage(transaction.getId(), Decision.Commit));
        final LogRecordEntity recordEntity = logRecordRepository.findOne(transaction.id());
        assertThat(recordEntity.getStatus()).isEqualTo(TransactionStatus.Commit);
        final Account afterTransfer = accountRepository.findOne(1);
        assertThat(afterTransfer.getBalance()).isEqualTo(balance - 100);

        final DecisionQueryMessage message = new DecisionQueryMessage();
        message.setTransactionId(transaction.id());
        final DecisionQueryResult decisionQueryResult = messageSender.sendDecisionQuery2Others(TestUtil.getBocNode(), message);
        assertThat(decisionQueryResult.getDecision()).isEqualTo(Decision.Commit);
    }

    @Test
    public void testHandler2() {
        final Account one = accountRepository.findOne(1);
        final long balance = one.getBalance();
        final DistributedTransactionImpl transaction = TestUtil.getWouldAbortTransaction();
        final VoteResult voteResult = messageSender.sendVoteRequest2Participant(TestUtil.getBocNode(), transaction);
        assertThat(voteResult.result()).isEqualTo(Vote.No);

        final LogRecordEntity recordEntity = logRecordRepository.findOne(transaction.id());
        assertThat(recordEntity.getStatus()).isEqualTo(TransactionStatus.Abort);
        final Account afterTransfer = accountRepository.findOne(1);
        assertThat(afterTransfer.getBalance()).isEqualTo(balance);

        final DecisionQueryMessage message = new DecisionQueryMessage();
        message.setTransactionId(transaction.id());
        final DecisionQueryResult decisionQueryResult = messageSender.sendDecisionQuery2Others(TestUtil.getBocNode(), message);
        assertThat(decisionQueryResult.getDecision()).isEqualTo(Decision.Abort);
    }
}
