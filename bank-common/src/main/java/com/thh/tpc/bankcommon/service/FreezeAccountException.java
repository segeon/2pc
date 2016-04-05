/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:04 创建
 *
 */
package com.thh.tpc.bankcommon.service;

/**
 * @author daidai@yiji.com
 */
public class FreezeAccountException extends RuntimeException {

    public FreezeAccountException() {
    }

    public FreezeAccountException(String message) {
        super(message);
    }

    public FreezeAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public FreezeAccountException(Throwable cause) {
        super(cause);
    }

    public FreezeAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
