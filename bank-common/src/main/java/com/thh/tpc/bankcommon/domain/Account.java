/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 21:51 创建
 *
 */
package com.thh.tpc.bankcommon.domain;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author daidai@yiji.com
 */
@Entity
@Table(name = "account", schema = "")
public class Account implements Serializable {
    private static final long serialVersionUID = -5252438015427982836L;
    private int accountId;
    private String userId;
    private long balance;
    private long frozenBalance;
    private Date addTime;
    private Date lastModifyTime;

    @Id
    @Column(name = "account_id")
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Column(name = "user_id", length = 32)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "balance")
    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Column(name = "frozen_balance")
    public long getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(long frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    @Column(name = "add_time")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Column(name = "last_modify_time")
    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                balance == account.balance &&
                frozenBalance == account.frozenBalance &&
                Objects.equal(userId, account.userId) &&
                Objects.equal(addTime, account.addTime) &&
                Objects.equal(lastModifyTime, account.lastModifyTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId, userId, balance, frozenBalance, addTime, lastModifyTime);
    }
}
