/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-02 14:59 创建
 *
 */
package com.thh.tpc.bankcommon.service;

import com.thh.tpc.TestBase;
import com.thh.tpc.bankcommon.domain.BankingParticipant;
import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.LogRecordRepository;
import com.thh.tpc.protocol.service.TransactionRepository;
import com.thh.tpc.protocol.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author daidai@yiji.com
 */
public class BankingParticipantTest extends TestBase {
    @Autowired
    private BankingParticipant bankingParticipant;
    @Autowired
    private LogRecordRepository logRecordRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testParticipantOperations() {
        final DistributedTransactionImpl transaction = TestUtil.getTransaction();
        final VoteResult vote = bankingParticipant.vote(transaction);
        assertThat(vote).isEqualTo(Vote.Yes);
        final LogRecordEntity recordEntity = logRecordRepository.findOne(transaction.id());
        assertThat(recordEntity).isNotNull();
        final TransactionEntity one = transactionRepository.findOne(transaction.id());
        assertThat(one).isNotNull();

        bankingParticipant.commit(transaction);
        final LogRecordEntity record = logRecordRepository.findOne(transaction.id());
        assertThat(record.getStatus()).isEqualTo(TransactionStatus.Commit);
    }

    @Test
    public void testParticipantOperations2() {
        final DistributedTransactionImpl transaction = TestUtil.getWouldAbortTransaction();
        final VoteResult vote = bankingParticipant.vote(transaction);
        assertThat(vote).isEqualTo(Vote.No);
        final LogRecordEntity recordEntity = logRecordRepository.findOne(transaction.id());
        assertThat(recordEntity).isNotNull();
        assertThat(recordEntity.getStatus()).isEqualTo(TransactionStatus.Abort);
    }
}
