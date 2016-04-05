/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 11:44 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.thh.tpc.protocol.domain.LogRecord;
import com.thh.tpc.protocol.domain.LogRecordImpl;
import com.thh.tpc.protocol.domain.TransactionStatus;
import com.thh.tpc.protocol.domain.DistributedTransaction;

import java.util.List;

/**
 * @author daidai@yiji.com
 */

public abstract class AbstractLogKeeper implements LogKeeper {

    @Override
    public void recordMsg(DistributedTransaction transaction, TransactionStatus type) {
        saveOrUpdate(createLogRecord(transaction, type));
    }

    private LogRecord createLogRecord(DistributedTransaction transaction, TransactionStatus status) {
        return new LogRecordImpl(transaction.id(), status, null, null);
    }

    public abstract void saveOrUpdate(LogRecord logRecord);

    public abstract void saveOrUpdate(DistributedTransaction distributedTransaction);

    public abstract List<LogRecord> findUndecidedTransactions();

    public abstract DistributedTransaction findTranactionById(String Id);

    public abstract List<DistributedTransaction> findTranactionByIds(List<String> Id);
}
