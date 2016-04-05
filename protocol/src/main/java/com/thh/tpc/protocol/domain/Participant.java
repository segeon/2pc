/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-24 23:09 创建
 *
 */
package com.thh.tpc.protocol.domain;

/**
 * @author daidai@yiji.com
 */
public interface Participant extends Node {

    VoteResult vote(DistributedTransaction transaction);

    void commit(DistributedTransaction transaction);

    void abort(DistributedTransaction transaction);

    void recover();

    Decision resolveDecisionByCTP(DistributedTransaction transaction);
}
