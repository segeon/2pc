/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:08 创建
 *
 */
package com.thh.tpc.bankcommon.service;

/**
 * @author daidai@yiji.com
 */
public class UnfreezeAccountException extends RuntimeException{
    public UnfreezeAccountException() {
        super();
    }

    public UnfreezeAccountException(String message) {
        super(message);
    }

    public UnfreezeAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnfreezeAccountException(Throwable cause) {
        super(cause);
    }

    protected UnfreezeAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
