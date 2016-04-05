/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 22:23 创建
 *
 */
package com.thh.tpc.protocol.handler;

/**
 * @author daidai@yiji.com
 */
public interface Handler {

    String command();

    String handle(String data);
}
