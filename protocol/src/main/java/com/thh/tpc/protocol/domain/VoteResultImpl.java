/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 21:13 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.google.common.base.Objects;

/**
 * @author daidai@yiji.com
 */
public class VoteResultImpl implements VoteResult {
    private String transactionId;
    private String voterName;
    private long voteTime;
    private Vote result;
    private String description;

    public VoteResultImpl() {
    }

    public VoteResultImpl(String transactionId, String voterName, long voteTime, Vote result, String description) {
        this.transactionId = transactionId;
        this.voterName = voterName;
        this.voteTime = voteTime;
        this.result = result;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getVoterName() {
        return voterName;
    }

    public long getVoteTime() {
        return voteTime;
    }

    public Vote getResult() {
        return result;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public void setVoteTime(long voteTime) {
        this.voteTime = voteTime;
    }

    public void setResult(Vote result) {
        this.result = result;
    }

    public String transactionId() {
        return transactionId;
    }

    public String voterName() {
        return voterName;
    }

    public long voteTime() {
        return voteTime;
    }

    public Vote result() {
        return result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteResultImpl that = (VoteResultImpl) o;
        return voteTime == that.voteTime &&
                Objects.equal(transactionId, that.transactionId) &&
                Objects.equal(voterName, that.voterName) &&
                result == that.result &&
                Objects.equal(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionId, voterName, voteTime, result, description);
    }
}
