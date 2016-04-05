/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:34 创建
 *
 */
package com.thh.tpc.bankcommon.service;

/**
 * @author daidai@yiji.com
 */
public class UnfreezeAndDeductException extends RuntimeException {
    public UnfreezeAndDeductException() {
    }

    public UnfreezeAndDeductException(String message) {
        super(message);
    }

    public UnfreezeAndDeductException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnfreezeAndDeductException(Throwable cause) {
        super(cause);
    }

    public UnfreezeAndDeductException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
