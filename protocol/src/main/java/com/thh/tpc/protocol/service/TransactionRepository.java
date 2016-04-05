/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 16:14 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.thh.tpc.protocol.domain.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;


/**
 * @author daidai@yiji.com
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    TransactionEntity findOne(String s);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    TransactionEntity save(TransactionEntity s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<TransactionEntity> findByIdIn(List<String> ids);
}
