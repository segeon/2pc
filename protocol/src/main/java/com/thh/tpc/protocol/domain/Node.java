/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-24 23:13 创建
 *
 */
package com.thh.tpc.protocol.domain;

import java.net.URL;

/**
 * @author daidai@yiji.com
 */
public interface Node {

    String name();

    URL communicationAddr();
}
