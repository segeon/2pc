/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 11:32 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.thh.tpc.protocol.domain.TransactionStatus;
import com.thh.tpc.protocol.domain.DistributedTransaction;

/**
 * @author daidai@yiji.com
 */
public interface LogKeeper {

    void recordMsg(DistributedTransaction transaction, TransactionStatus type);
}
