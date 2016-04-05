/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 15:58 创建
 *
 */
package com.thh.tpc.protocol.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author daidai@yiji.com
 */
@Entity
@Table(name = "log_record", schema = "")
public class LogRecordEntity implements Serializable{
    private static final long serialVersionUID = -3504908030346251462L;
    private String transactionId;
    private TransactionStatus status;
    private Timestamp createTime;
    private Timestamp lastModifyTime;

    @Id
    @Column(name = "transaction_id", length = 32)
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "status", nullable = false)
    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Column(name = "create_time", nullable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Column(name = "modify_time", nullable = true)
    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Timestamp lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}
