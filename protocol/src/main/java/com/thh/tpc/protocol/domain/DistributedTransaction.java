/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-24 23:01 创建
 *
 */
package com.thh.tpc.protocol.domain;

import java.util.List;

/**
 * @author daidai@yiji.com
 */
public interface DistributedTransaction {

    String id();

    List<Action> requestedActions();

    List<VoteResult> voteResults();

    List<Node> participants();
}
