/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-24 23:04 创建
 *
 */
package com.thh.tpc.protocol.domain;

import java.util.List;

/**
 * @author daidai@yiji.com
 */
public interface Coordinator extends Node {

    /**
     * 获取所有participants的名字和Node映射关系
     * @return
     */
    List<Node> participants();

    void sendVoteRequest(DistributedTransaction transaction);

    Decision makeDecision(DistributedTransaction transaction);

    void notifyDecision(DistributedTransaction transaction, Decision decision);

    void recover();
}
