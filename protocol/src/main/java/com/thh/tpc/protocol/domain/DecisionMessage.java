/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 11:18 创建
 *
 */
package com.thh.tpc.protocol.domain;

/**
 * @author daidai@yiji.com
 */

/**
 * Coordinator 做出决定后 发送给 participants 的通知消息
 */
public class DecisionMessage {
    private String transactionId;
    private Decision decision;

    public DecisionMessage() {
    }

    public DecisionMessage(String transactionId, Decision decision) {
        this.transactionId = transactionId;
        this.decision = decision;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
