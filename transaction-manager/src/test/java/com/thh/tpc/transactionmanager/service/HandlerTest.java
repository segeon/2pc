/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-30 22:11 创建
 *
 */
package com.thh.tpc.transactionmanager.service;

import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.MessageSender;
import com.thh.tpc.protocol.util.TestUtil;
import com.thh.tpc.transactionmanager.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author daidai@yiji.com
 */
public class HandlerTest extends TestBase{
    @Autowired
    private MessageSender messageSender;

    @Test
    public void testTransRequestHandler() {
        final DistributedTransactionImpl transaction = TestUtil.getTransaction();
        final TransReqResult result = messageSender.sendTransactionRequest2Coordinator(TestUtil.getCoordinatorNode(), transaction);
        assertThat(result.getTransactionId()).isEqualTo(transaction.getId());
    }

    @Test
    public void testDecisionQueryHandler() {
        final DistributedTransactionImpl transaction = TestUtil.getTransaction();
        final DecisionQueryMessage decisionQueryMessage = new DecisionQueryMessage();
        decisionQueryMessage.setTransactionId(transaction.id());
        final DecisionQueryResult result = messageSender.sendDecisionQuery2Others(TestUtil.getCoordinatorNode(), decisionQueryMessage);
        assertThat(result.getTransactionId()).isEqualTo(transaction.getId());
        assertThat(result.getDecision()).isEqualTo(Decision.Unknown);
    }
}

