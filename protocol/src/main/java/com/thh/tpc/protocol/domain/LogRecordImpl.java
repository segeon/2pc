/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 15:43 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.google.common.base.Objects;

import java.util.Date;

/**
 * @author daidai@yiji.com
 */
public class LogRecordImpl implements LogRecord {
    private String transactionId;
    private TransactionStatus status;
    private Date createTime;
    private Date lastModifyTime;

    public LogRecordImpl() {
    }

    public LogRecordImpl(String transactionId, TransactionStatus status, Date createTime, Date lastModifyTime) {
        this.transactionId = transactionId;
        this.status = status;
        this.createTime = createTime;
        this.lastModifyTime = lastModifyTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public String transactionId() {
        return transactionId;
    }

    @Override
    public TransactionStatus status() {
        return status;
    }

    @Override
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public Date createTime() {
        return createTime;
    }

    @Override
    public Date lastModifyTime() {
        return lastModifyTime;
    }

    @Override
    public void setModifyTime(Date date) {
        this.lastModifyTime = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogRecordImpl logRecord = (LogRecordImpl) o;
        return Objects.equal(transactionId, logRecord.transactionId) &&
                status == logRecord.status &&
                Objects.equal(createTime, logRecord.createTime) &&
                Objects.equal(lastModifyTime, logRecord.lastModifyTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionId, status, createTime, lastModifyTime);
    }
}
