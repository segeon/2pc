/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 16:17 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.thh.tpc.protocol.domain.LogRecord;
import com.thh.tpc.protocol.domain.LogRecordEntity;
import com.thh.tpc.protocol.domain.LogRecordImpl;
import com.thh.tpc.protocol.domain.TransactionStatus;
import com.thh.tpc.protocol.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
@Service
public class LogRecordService {
    @Autowired
    private LogRecordRepository logRecordRepository;

    public void saveOrUpdate(LogRecord logRecord) {
        LogRecordEntity one = logRecordRepository.findOne(logRecord.transactionId());
        if (one == null) {
            one = new LogRecordEntity();
            one.setTransactionId(logRecord.transactionId());
            one.setStatus(logRecord.status());
            one.setCreateTime(new Timestamp(new Date().getTime()));
            one.setLastModifyTime(new Timestamp(new Date().getTime()));
        } else {
            one.setStatus(logRecord.status());
            one.setLastModifyTime(new Timestamp(new Date().getTime()));
        }
        logRecordRepository.save(one);
    }

    public LogRecord findOne(String transactionId) {
        final LogRecordEntity one = logRecordRepository.findOne(transactionId);
        return convert(one);
    }

    public LogRecord convert(LogRecordEntity entity) {
        final LogRecordImpl logRecord = new LogRecordImpl();
        logRecord.setTransactionId(entity.getTransactionId());
        logRecord.setStatus(entity.getStatus());
        logRecord.setCreateTime(entity.getCreateTime());
        logRecord.setLastModifyTime(entity.getLastModifyTime());
        return logRecord;
    }

    @Transactional
    public List<LogRecord> findUndecidedTransactions() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis() - Constants.DEFAULT_WAIT_FOR_DECISION_TIMEOUT_SECS * 1000);
        final List<LogRecordEntity> logRecordEntities = logRecordRepository.findByStatusInAndCreateTimeLessThan(Arrays.asList(TransactionStatus.Start2PC), timestamp);
        final ArrayList<LogRecord> objects = new ArrayList<>(logRecordEntities.size());
        logRecordEntities.forEach(entity -> {
            objects.add(new LogRecordImpl(entity.getTransactionId(), entity.getStatus(), entity.getCreateTime(), entity.getLastModifyTime()));
        });
        return objects;
    }
}
