/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 14:51 创建
 *
 */
package com.thh.tpc.protocol.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author daidai@yiji.com
 */
public interface LogRecord extends Serializable{

    String transactionId();

    TransactionStatus status();

    void setStatus(TransactionStatus status);

    Date createTime();

    Date lastModifyTime();

    void setModifyTime(Date date);
}
