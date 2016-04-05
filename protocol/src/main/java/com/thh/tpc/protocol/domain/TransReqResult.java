/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 22:30 创建
 *
 */
package com.thh.tpc.protocol.domain;

import java.io.Serializable;

/**
 * @author daidai@yiji.com
 */
public class TransReqResult implements Serializable{
    private String transactionId;
    private Decision decision;
    private String description;

    public TransReqResult() {
    }

    public TransReqResult(String transactionId, Decision decision, String description) {
        this.transactionId = transactionId;
        this.decision = decision;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
