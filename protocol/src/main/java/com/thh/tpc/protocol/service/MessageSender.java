/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 22:42 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.thh.tpc.protocol.domain.*;

/**
 * @author daidai@yiji.com
 */
public interface MessageSender {

    TransReqResult sendTransactionRequest2Coordinator(final Node node, DistributedTransaction transaction);

    VoteResult sendVoteRequest2Participant(final Node node, DistributedTransaction transaction);

    void sendDecision2Participant(final Node node, DecisionMessage message);

    DecisionQueryResult sendDecisionQuery2Others(final Node node, DecisionQueryMessage message);
}
