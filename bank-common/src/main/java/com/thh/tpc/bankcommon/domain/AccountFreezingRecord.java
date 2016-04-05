/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 22:00 创建
 *
 */
package com.thh.tpc.bankcommon.domain;

import com.google.common.base.Objects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author daidai@yiji.com
 */
@Entity
@Table(name = "account_freezing_record", schema = "",
        indexes = {@Index(name = "account_id_idx", columnList = "account_id"), @Index(name = "trans_idx", columnList = "transaction_id")})
public class AccountFreezingRecord implements Serializable {
    private static final long serialVersionUID = -5360334060609945020L;
    private int id;
    private int accountId;
    private long frozenAmount;
    private String transactionId;
    private Date freezingTime;
    private Date unfreezingTime;
    private boolean valid;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "account_id", nullable = false, updatable = false)
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Column(name = "frozen_amount", nullable = false, updatable = false)
    public long getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(long frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    @Column(name = "transaction_id", nullable = true, updatable = false, unique = true)
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "freezing_time", nullable = false, updatable = false)
    public Date getFreezingTime() {
        return freezingTime;
    }

    public void setFreezingTime(Date freezingTime) {
        this.freezingTime = freezingTime;
    }

    @Column(name = "unfreezing_time")
    public Date getUnfreezingTime() {
        return unfreezingTime;
    }

    public void setUnfreezingTime(Date unfreezingTime) {
        this.unfreezingTime = unfreezingTime;
    }

    @Column(name = "valid")
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountFreezingRecord that = (AccountFreezingRecord) o;
        return id == that.id &&
                accountId == that.accountId &&
                frozenAmount == that.frozenAmount &&
                valid == that.valid &&
                Objects.equal(transactionId, that.transactionId) &&
                Objects.equal(freezingTime, that.freezingTime) &&
                Objects.equal(unfreezingTime, that.unfreezingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, accountId, frozenAmount, transactionId, freezingTime, unfreezingTime, valid);
    }
}
