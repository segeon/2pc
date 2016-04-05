/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-29 21:11 创建
 *
 */
package com.thh.tpc.protocol.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.thh.tpc.protocol.domain.AbstractParticipant;
import com.thh.tpc.protocol.domain.Decision;
import com.thh.tpc.protocol.domain.DecisionMessage;
import com.thh.tpc.protocol.domain.DistributedTransaction;
import com.thh.tpc.protocol.service.TransactionService;
import com.thh.tpc.protocol.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author daidai@yiji.com
 */
@Component
@ConditionalOnBean(AbstractParticipant.class)
public class DecisionHandler implements Handler {
    private static final String command = Constants.DECISION_COMMAND;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AbstractParticipant bankingParticipant;
    @Autowired
    private TransactionService transactionService;

    @Override
    public String command() {
        return command;
    }

    @Override
    public String handle(String data) {
        if (Strings.isNullOrEmpty(data)) {
            logger.error("DecisionMessage为空!");
            return Constants.RECEIVE_DECISION_ERROR;
        }
        DecisionMessage decisionMessage;
        try {
            decisionMessage = JSON.parseObject(data, DecisionMessage.class);
        } catch (Exception e) {
            logger.error("解析DecisionMessage失败: {}", data, e);
            return Constants.RECEIVE_DECISION_ERROR;
        }
        final String transactionId = decisionMessage.getTransactionId();
        final DistributedTransaction transaction = transactionService.findTranactionById(transactionId);
        if (transaction == null) {
            logger.error("没找到transactionId: " + transactionId);
            return Constants.RECEIVE_DECISION_ERROR;
        }
        logger.info("收到transaction: {} 的 decision: {}", transactionId, decisionMessage.getDecision().name());
        try {
            if (decisionMessage.getDecision().equals(Decision.Abort)) {
                bankingParticipant.abort(transaction);
                return Constants.RECEIVE_DECISION_OK;
            } else if (decisionMessage.getDecision().equals(Decision.Commit)){
                bankingParticipant.commit(transaction);
                return Constants.RECEIVE_DECISION_OK;
            }
        } catch (Exception e) {
            logger.info("abort or commit 出错!", e);
            return Constants.RECEIVE_DECISION_ERROR;
        }
        if (decisionMessage.getDecision().equals(Decision.Unknown)) {
            logger.error("接收到 Unkown decision! transactionId={}", transactionId);
            throw new IllegalArgumentException("接收到 Unkown decision!");
        }
        return Constants.RECEIVE_DECISION_OK;
    }
}
