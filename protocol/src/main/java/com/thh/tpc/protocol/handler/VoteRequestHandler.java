/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-29 21:03 创建
 *
 */
package com.thh.tpc.protocol.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Strings;
import com.thh.tpc.protocol.domain.*;
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
public class VoteRequestHandler implements Handler {
    private static final String command = Constants.VOTE_REQ_COMMAND;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AbstractParticipant bankingParticipant;

    @Override
    public String command() {
        return command;
    }

    @Override
    public String handle(String data) {
        if (Strings.isNullOrEmpty(data)) {
            final VoteResultImpl voteResult = new VoteResultImpl(null, bankingParticipant.name(),
                    System.currentTimeMillis(), Vote.No, "请求data不能为空!");
            return JSON.toJSONString(voteResult, SerializerFeature.WriteClassName);
        }
        DistributedTransactionImpl transaction;
        try {
            transaction = JSON.parseObject(data, DistributedTransactionImpl.class);
        } catch (Exception e) {
            logger.error("解析DistributedTransaction失败: {}", data, e);
            final VoteResultImpl voteResult = new VoteResultImpl(null, bankingParticipant.name(),
                    System.currentTimeMillis(), Vote.No, "解析DistributedTransaction失败");
            return JSON.toJSONString(voteResult, SerializerFeature.WriteClassName);
        }
        final VoteResult vote = bankingParticipant.vote(transaction);
        logger.info("{} votes {} for {}, message={}", bankingParticipant.name(), vote.result().name(), transaction.id(), vote.getDescription());
        return JSON.toJSONString(vote, SerializerFeature.WriteClassName);
    }
}
