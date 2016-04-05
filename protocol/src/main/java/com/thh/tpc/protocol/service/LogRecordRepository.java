/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 16:10 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.thh.tpc.protocol.domain.LogRecordEntity;
import com.thh.tpc.protocol.domain.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
@Repository
public interface LogRecordRepository extends JpaRepository<LogRecordEntity, String>{

    @Lock(LockModeType.PESSIMISTIC_READ)
    LogRecordEntity findOne(String transactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    LogRecordEntity save(LogRecordEntity s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<LogRecordEntity> findByStatusInAndCreateTimeLessThan(List<TransactionStatus> status, Timestamp timestamp);
}
