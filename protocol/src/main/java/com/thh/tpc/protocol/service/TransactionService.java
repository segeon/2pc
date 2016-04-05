/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 16:34 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.alibaba.fastjson.JSON;
import com.thh.tpc.protocol.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdate(DistributedTransaction distributedTransaction) {
        TransactionEntity one = transactionRepository.findOne(distributedTransaction.id());
        if (one == null) {
            one = new TransactionEntity();
            one.setId(distributedTransaction.id());
            one.setActions(JSON.toJSONString(distributedTransaction.requestedActions()));
            one.setVoteResults(JSON.toJSONString(distributedTransaction.voteResults()));
            one.setParticipants(JSON.toJSONString(distributedTransaction.participants()));
            one.setCreateTime(new Date());
            one.setUpdateTime(new Date());
        } else {
            one.setActions(JSON.toJSONString(distributedTransaction.requestedActions()));
            one.setVoteResults(JSON.toJSONString(distributedTransaction.voteResults()));
            one.setParticipants(JSON.toJSONString(distributedTransaction.participants()));
            one.setUpdateTime(new Date());
        }
        transactionRepository.save(one);
    }

    public DistributedTransaction findTranactionById(String Id) {
        final TransactionEntity transactionEntity = transactionRepository.findOne(Id);
        if (transactionEntity == null)
            return null;
        return getDistributedTransaction(transactionEntity);
    }

    private DistributedTransactionImpl getDistributedTransaction(TransactionEntity transactionEntity) {
        final List<TransferAction> transferActions = JSON.parseArray(transactionEntity.getActions(), TransferAction.class);
        final ArrayList<Action> actions = new ArrayList<>(transferActions.size());
        transferActions.forEach(transferAction -> actions.add(transferAction));
        final List<VoteResultImpl> voteResults = JSON.parseArray(transactionEntity.getVoteResults(), VoteResultImpl.class);
        final ArrayList<VoteResult> results = new ArrayList<>(voteResults.size());
        voteResults.forEach(voteResult -> results.add(voteResult));
        final List<NodeImpl> nodes = JSON.parseArray(transactionEntity.getParticipants(), NodeImpl.class);
        final ArrayList<Node> list = new ArrayList<>(nodes.size());
        nodes.forEach(node -> list.add(node));
        return new DistributedTransactionImpl(transactionEntity.getId(), actions, results, list);
    }

    @Transactional
    public List<DistributedTransaction> findTranactionByIds(List<String> Id) {
        final LinkedList<DistributedTransaction> objects = new LinkedList<>();
        final List<TransactionEntity> entities = transactionRepository.findByIdIn(Id);
        entities.forEach(entity -> objects.add(getDistributedTransaction(entity)));
        return objects;
    }
}
