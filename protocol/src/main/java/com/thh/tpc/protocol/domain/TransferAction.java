/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 18:47 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.google.common.base.Objects;

import java.util.List;

/**
 * @author daidai@yiji.com
 */
public class TransferAction implements Action {

    private String fromBank;

    private String fromAccount;

    private String fromUserName;

    private String toBank;

    private String toAccount;

    private String toUserName;

    private long amount;


    public TransferAction() {
    }

    public TransferAction(String fromBank, String fromAccount, String toBank, String toAccount, long amount) {
        this.fromBank = fromBank;
        this.fromAccount = fromAccount;
        this.toBank = toBank;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromBank() {
        return fromBank;
    }

    public void setFromBank(String fromBank) {
        this.fromBank = fromBank;
    }

    public String getToBank() {
        return toBank;
    }

    public void setToBank(String toBank) {
        this.toBank = toBank;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String action() {
        return "wireTransfer";
    }

    public List<Object> params() {
        return null;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferAction that = (TransferAction) o;
        return amount == that.amount &&
                Objects.equal(fromBank, that.fromBank) &&
                Objects.equal(fromAccount, that.fromAccount) &&
                Objects.equal(fromUserName, that.fromUserName) &&
                Objects.equal(toBank, that.toBank) &&
                Objects.equal(toAccount, that.toAccount) &&
                Objects.equal(toUserName, that.toUserName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fromBank, fromAccount, fromUserName, toBank, toAccount, toUserName, amount);
    }
}
