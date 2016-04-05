package com.thh.tpc.transactionmanager.service;

import com.thh.tpc.protocol.domain.LogRecordEntity;
import com.thh.tpc.protocol.domain.TransactionStatus;
import com.thh.tpc.protocol.service.LogRecordRepository;
import com.thh.tpc.protocol.util.TransactionIdGenerator;
import com.thh.tpc.transactionmanager.TestBase;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author daidai@yiji.com
 */
public class LogRecordRepositoryTest extends TestBase{
    private LogRecordRepository logRecordRepository;
    private static final String id = TransactionIdGenerator.getTransactionId();


    @Test
    public void testFindOne() throws Exception {
        final LogRecordEntity logRecordEntity = new LogRecordEntity();
        logRecordEntity.setTransactionId(id);
        logRecordEntity.setStatus(TransactionStatus.Start2PC);
        logRecordEntity.setCreateTime(new Timestamp(new Date().getTime()));
        logRecordEntity.setLastModifyTime(new Timestamp(new Date().getTime()));
        logRecordRepository.save(logRecordEntity);

        final LogRecordEntity one = logRecordRepository.findOne(id);
        assertThat(one).isNotNull();

        final List<LogRecordEntity> byStatusIn = logRecordRepository.findByStatusInAndCreateTimeLessThan(
                Arrays.asList(TransactionStatus.Start2PC, TransactionStatus.No), new Timestamp(System.currentTimeMillis()));
        assertThat(byStatusIn).isNotEmpty();
    }

}