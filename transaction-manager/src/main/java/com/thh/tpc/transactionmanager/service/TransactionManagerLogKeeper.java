/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 15:39 创建
 *
 */
package com.thh.tpc.transactionmanager.service;

import com.thh.tpc.protocol.domain.DistributedTransaction;
import com.thh.tpc.protocol.domain.LogRecord;
import com.thh.tpc.protocol.service.AbstractLogKeeper;
import com.thh.tpc.protocol.service.LogRecordService;
import com.thh.tpc.protocol.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author daidai@yiji.com
 */
@Component
public class TransactionManagerLogKeeper extends AbstractLogKeeper {
    @Autowired
    private LogRecordService logRecordService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public void saveOrUpdate(LogRecord logRecord) {
        logRecordService.saveOrUpdate(logRecord);
    }

    @Override
    public void saveOrUpdate(DistributedTransaction distributedTransaction) {
        transactionService.saveOrUpdate(distributedTransaction);
    }

    @Override
    public List<LogRecord> findUndecidedTransactions() {
        return logRecordService.findUndecidedTransactions();
    }

    @Override
    public DistributedTransaction findTranactionById(String Id) {
        return transactionService.findTranactionById(Id);
    }

    @Override
    public List<DistributedTransaction> findTranactionByIds(List<String> Id) {
        return transactionService.findTranactionByIds(Id);
    }
}
