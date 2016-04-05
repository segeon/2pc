/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-29 21:31 创建
 *
 */
package com.thh.tpc.protocol.domain;

/**
 * @author daidai@yiji.com
 */
public class DecisionQueryMessage {
    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
