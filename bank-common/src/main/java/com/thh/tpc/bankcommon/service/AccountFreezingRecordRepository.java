/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 22:25 创建
 *
 */
package com.thh.tpc.bankcommon.service;

import com.thh.tpc.bankcommon.domain.AccountFreezingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

/**
 * @author daidai@yiji.com
 */
@Repository
public interface AccountFreezingRecordRepository extends JpaRepository<AccountFreezingRecord, Integer>{

    @Lock(LockModeType.PESSIMISTIC_READ)
    AccountFreezingRecord findOne(Integer integer);

    @Lock(LockModeType.PESSIMISTIC_READ)
    AccountFreezingRecord findByAccountId(Integer accountId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    AccountFreezingRecord findByTransactionId(String transactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void deleteByTransactionId(String transactionId);

}
