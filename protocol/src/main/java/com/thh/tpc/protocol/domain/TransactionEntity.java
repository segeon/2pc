/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 16:04 创建
 *
 */
package com.thh.tpc.protocol.domain;


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
@Table(name = "transactions", schema = "")
public class TransactionEntity implements Serializable{
    private static final long serialVersionUID = 6398467983222688070L;
    private String id;
    private String actions;
    private String voteResults;
    private String participants;
    private Date createTime;
    private Date updateTime;

    @Id
    @Column(name = "id", length = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "actions", length = 1024)
    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    @Column(name = "voteResults", length = 512)
    public String getVoteResults() {
        return voteResults;
    }

    public void setVoteResults(String voteResults) {
        this.voteResults = voteResults;
    }

    @Column(name = "participants", length = 256)
    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    @Column(name = "create_time", nullable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time", nullable = true)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(actions, that.actions) &&
                Objects.equal(voteResults, that.voteResults) &&
                Objects.equal(participants, that.participants) &&
                Objects.equal(createTime, that.createTime) &&
                Objects.equal(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, actions, voteResults, participants, createTime, updateTime);
    }
}
