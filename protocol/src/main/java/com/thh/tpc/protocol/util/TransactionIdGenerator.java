/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-26 21:06 创建
 *
 */
package com.thh.tpc.protocol.util;

import java.util.UUID;

/**
 * @author daidai@yiji.com
 */
public class TransactionIdGenerator {

    public static String getTransactionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
