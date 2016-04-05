/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-29 21:32 创建
 *
 */
package com.thh.tpc.protocol.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.LogRecordRepository;
import com.thh.tpc.protocol.service.TransactionService;
import com.thh.tpc.protocol.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author daidai@yiji.com
 */
@Component
public class ParticipantDecisionQueryHandler implements Handler {
    private static String command = Constants.DECISION_QUERY_COMMAND;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LogRecordRepository logRecordRepository;

    @Override
    public String command() {
        return command;
    }

    @Override
    public String handle(String data) {
        if (Strings.isNullOrEmpty(data)) {
            logger.error("DecisionQueryMessage为空!");
            return getDecisionString(null, Decision.Abort, "DecisionQueryMessage为空!");
        }
        DecisionQueryMessage decisionQueryMessage;
        try {
            decisionQueryMessage = JSON.parseObject(data, DecisionQueryMessage.class);
        } catch (Exception e) {
            logger.error("解析DecisionQueryMessage失败: {}", data, e);
            return getDecisionString(null, Decision.Abort, "解析DecisionQueryMessage失败");
        }

        final String transactionId = decisionQueryMessage.getTransactionId();
        LogRecordEntity recordEntity = logRecordRepository.findOne(transactionId);

        if (TransactionStatus.isFinalStatus(recordEntity.getStatus())) {
            return getDecisionString(transactionId, recordEntity.getStatus().equals(TransactionStatus.Commit)
                    ? Decision.Commit : Decision.Abort, "");
        } else {
            return getDecisionString(transactionId, Decision.Unknown, "Decision is currently unknown, please try consulting others.");
        }
    }

    private String getDecisionString(String transactionId, Decision decision, String des) {
        final DecisionQueryResult decisionMessage = new DecisionQueryResult(transactionId, decision, des);
        return JSON.toJSONString(decisionMessage);
    }
}
