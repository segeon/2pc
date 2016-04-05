/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-24 23:29 创建
 *
 */
package com.thh.tpc.protocol.domain;

/**
 * @author daidai@yiji.com
 */
public interface VoteResult {

    String transactionId();

    String voterName();

    long voteTime();

    Vote result();

    public String getDescription();
}
