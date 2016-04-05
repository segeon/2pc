/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 11:39 创建
 *
 */
package com.thh.tpc.protocol.domain;

/**
 * @author daidai@yiji.com
 */
public enum TransactionStatus {
    Start2PC,
    Abort,
    Commit,
    Yes,
    No;

    public static boolean isFinalStatus(TransactionStatus status) {
        return status.equals(Abort) || status.equals(Commit);
    }
}
