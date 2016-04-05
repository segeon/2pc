package com.thh.tpc.transactionmanager.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.LogRecordRepository;
import com.thh.tpc.protocol.service.LogRecordService;
import com.thh.tpc.protocol.service.TransactionService;
import com.thh.tpc.protocol.util.TestUtil;
import com.thh.tpc.protocol.util.TransactionIdGenerator;
import com.thh.tpc.transactionmanager.TestBase;


/**
 * @author daidai@yiji.com
 */
public class TransactionServiceTest extends TestBase {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LogRecordService logRecordService;
    @Autowired
    private LogRecordRepository logRecordRepository;

    @Test
    //@Ignore
    public void testSaveTransaction() throws Exception {
        final DistributedTransactionImpl distributedTransaction = TestUtil.getTransaction();
        transactionService.saveOrUpdate(distributedTransaction);
        final DistributedTransaction transaction = transactionService.findTranactionById(distributedTransaction.id());
        assertThat(transaction).isEqualTo(distributedTransaction);
    }

    @Test
    //@Ignore
    public void testSaveLogRecord(){
        final LogRecordImpl logRecord = new LogRecordImpl();
        final String transactionId = TransactionIdGenerator.getTransactionId();
        logRecord.setTransactionId(transactionId);
        logRecord.setStatus(TransactionStatus.Start2PC);
        final Date date = new Timestamp(new Date().getTime());
        logRecord.setCreateTime(date);
        logRecord.setLastModifyTime(date);
        logRecordService.saveOrUpdate(logRecord);
        final LogRecord one = logRecordService.findOne(transactionId);
        assertThat(one.transactionId()).isEqualTo(logRecord.transactionId());
        assertThat(one.status()).isEqualTo(logRecord.status());
        final List<LogRecord> undecidedTransactions = logRecordService.findUndecidedTransactions();
        assertThat(undecidedTransactions).isNotEmpty();
        boolean find = false;
        for (LogRecord transaction : undecidedTransactions) {
            if (transaction.transactionId().equals(transactionId)) {
                find = true;
                break;
            }
        }
        assertThat(find).isTrue();
    }

}