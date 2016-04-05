/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 22:19 创建
 *
 */
package com.thh.tpc.bankcommon.service;

import com.thh.tpc.bankcommon.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

/**
 * @author daidai@yiji.com
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Account findOne(Integer accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account save(Account s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Account findByUserId(String userId);
}
