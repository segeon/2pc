/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 22:44 创建
 *
 */
package com.thh.tpc.protocol.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.util.Constants;
import com.thh.tpc.protocol.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author daidai@yiji.com
 */
@Component
public class MessageSenderImpl implements MessageSender {


    private Logger logger = LoggerFactory.getLogger(getClass());
    private int waitForVoteTimeoutSecs;
    private HttpUtil httpUtil;

    public MessageSenderImpl() {
        this(Constants.DEFAULT_WAIT_FOR_VOTE_TIMEOUT_SECS);
    }

    public MessageSenderImpl(int waitForVoteTimeoutSecs) {
        this.waitForVoteTimeoutSecs = waitForVoteTimeoutSecs;
        httpUtil = HttpUtil.getInstance().readTimeout(waitForVoteTimeoutSecs);
    }

    public int getWaitForVoteTimeoutSecs() {
        return waitForVoteTimeoutSecs;
    }

    public void setWaitForVoteTimeoutSecs(int waitForVoteTimeoutSecs) {
        if (waitForVoteTimeoutSecs > 0) {
            this.waitForVoteTimeoutSecs = waitForVoteTimeoutSecs;
            httpUtil.readTimeout(waitForVoteTimeoutSecs);
        }
    }

    @Override
    public TransReqResult sendTransactionRequest2Coordinator(Node node, DistributedTransaction transaction) {
        final String httpURL = getHttpURL(node, Constants.TRANSACTION_REQ_COMMAND);
        final HashMap<String, String> map = new HashMap<>();
        map.put("data", JSON.toJSONString(transaction, SerializerFeature.WriteClassName));
        final HttpUtil.HttpResult result;
        try {
            result = httpUtil.post(httpURL, map);
        } catch (Exception e) {
            logger.error("发送transaction(id={})到coordinator({})失败:", transaction.id(), httpURL, e);
            return new TransReqResult(transaction.id(), Decision.Unknown, "发送请求失败!");
        }
        try {
            return JSON.parseObject(result.getBody(), TransReqResult.class);
        } catch (Exception e) {
            logger.error("解析请求返回结果失败: {}", result.getBody(), e);
            return new TransReqResult(transaction.id(), Decision.Unknown, "解析结果失败!");
        }
    }

    @Override
    public VoteResult sendVoteRequest2Participant(Node node, DistributedTransaction transaction) {
        final String httpURL = getHttpURL(node, Constants.VOTE_REQ_COMMAND);
        final HashMap<String, String> map = new HashMap<>();
        map.put("data", JSON.toJSONString(transaction, SerializerFeature.WriteClassName));
        final VoteResultImpl voteResult = new VoteResultImpl(transaction.id(), node.name(), 0, Vote.Unknow, "unknown yet");
        try {
            final HttpUtil.HttpResult postRes = httpUtil.post(httpURL, map);
            if (postRes == null || postRes.getStatusCode() != 200) {
                logger.error("发送VoteRequest请求失败: transactionId={}", transaction.id());
                voteResult.setVoteTime(System.currentTimeMillis());
                voteResult.setResult(Vote.No);
            } else {
                try {
                    final VoteResultImpl ret = JSON.parseObject(postRes.getBody(), VoteResultImpl.class);
                    return ret;
                } catch (Exception e) {
                    logger.error("解析VoteResult出错", e);
                    voteResult.setVoteTime(System.currentTimeMillis());
                    voteResult.setResult(Vote.No);
                }
            }
        } catch (Exception e) {
            logger.error("发送VoteRequest请求异常: ", e);
            voteResult.setVoteTime(System.currentTimeMillis());
            voteResult.setResult(Vote.No);
        }
        return voteResult;
    }

    private String getHttpURL(Node node, String command) {
        return node.communicationAddr().toString() + "?" + Constants.COMMAND_KEY + "=" + command;
    }

    @Override
    public void sendDecision2Participant(Node node, DecisionMessage message) {
        final String httpURL = getHttpURL(node, Constants.DECISION_COMMAND);
        final HashMap<String, String> map = new HashMap<>();
        map.put("data", JSON.toJSONString(message, SerializerFeature.WriteClassName));
        try {
            final HttpUtil.HttpResult result = httpUtil.post(httpURL, map);
        } catch (Exception e) {
            logger.error("通知decision到{}失败:", httpURL, e);
        }
    }

    @Override
    public DecisionQueryResult sendDecisionQuery2Others(final Node node, DecisionQueryMessage message) {
        final String httpURL = getHttpURL(node, Constants.DECISION_QUERY_COMMAND);
        final HashMap<String, String> map = new HashMap<>();
        map.put("data", JSON.toJSONString(message, SerializerFeature.WriteClassName));
        try {
            final HttpUtil.HttpResult result = httpUtil.post(httpURL, map);
            final DecisionQueryResult queryResult = JSON.parseObject(result.getBody(), DecisionQueryResult.class);
            return queryResult;
        } catch (Exception e) {
            logger.error("向{}查询transaction({})的decision异常: ", httpURL, message.getTransactionId(), e);
            return new DecisionQueryResult(message.getTransactionId(), Decision.Unknown, e.getMessage());
        }
    }
}
