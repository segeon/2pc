/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 22:49 创建
 *
 */
package com.thh.tpc.protocol.util;

/**
 * @author daidai@yiji.com
 */
public class Constants {
    /* commands */
    public static final String COMMAND_KEY = "command";
    public static final String TRANSACTION_REQ_COMMAND = "transactionReq";
    public static final String VOTE_REQ_COMMAND = "voteReq";
    public static final String DECISION_COMMAND = "decision";
    public static final String DECISION_QUERY_COMMAND = "decisionQuery";


    /* 投票结果通知接收时回复消息, 没啥用 */
    public static final String RECEIVE_DECISION_OK = "ok";
    public static final String RECEIVE_DECISION_ERROR = "error";

    /* Coordinator发送投票请求时,等待Participant投票结果的超时时间 */
    public static final int DEFAULT_WAIT_FOR_VOTE_TIMEOUT_SECS = 2 * 60;

    /* Participant等待投票决议的超时时间 */
    public static final int DEFAULT_WAIT_FOR_DECISION_TIMEOUT_SECS = 2 * 60;

    /* 定期执行恢复操作的时间间隔, 以秒为单位 */
    public static final int RECOVER_INTERVAL_SECS = 60;
}
