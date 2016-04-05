/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 21:16 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.google.common.base.Objects;
import com.thh.tpc.protocol.util.TransactionIdGenerator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
public class DistributedTransactionImpl implements DistributedTransaction {
    private String id;
    private List<Action> actions;
    private List<VoteResult> voteResults;
    private List<Node> participants;

    public DistributedTransactionImpl() {
    }

    public DistributedTransactionImpl(List<Action> actions) {
        this(TransactionIdGenerator.getTransactionId(), actions, new LinkedList<>(), new LinkedList<Node>());
    }

    public DistributedTransactionImpl(String id, List<Action> actions, List<VoteResult> voteResults, List<Node> participants) {
        this.id = id;
        this.actions = actions;
        this.voteResults = voteResults;
        this.participants = participants;
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Action> requestedActions() {
        return actions;
    }

    public List<VoteResult> voteResults() {
        return voteResults;
    }

    public List<Node> participants() {
        return participants;
    }

    public String getId() {
        return id;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<VoteResult> getVoteResults() {
        return voteResults;
    }

    public List<Node> getParticipants() {
        return participants;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void setVoteResults(List<VoteResult> voteResults) {
        this.voteResults = voteResults;
    }

    public void setParticipants(List<Node> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistributedTransactionImpl that = (DistributedTransactionImpl) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(actions, that.actions) &&
                Objects.equal(voteResults, that.voteResults) &&
                Objects.equal(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, actions, voteResults, participants);
    }
}
