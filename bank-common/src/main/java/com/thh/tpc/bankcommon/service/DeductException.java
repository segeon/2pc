/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:31 创建
 *
 */
package com.thh.tpc.bankcommon.service;

/**
 * @author daidai@yiji.com
 */
public class DeductException extends RuntimeException{
    public DeductException() {
    }

    public DeductException(String message) {
        super(message);
    }

    public DeductException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeductException(Throwable cause) {
        super(cause);
    }

    public DeductException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
