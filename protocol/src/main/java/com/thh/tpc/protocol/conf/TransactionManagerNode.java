/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-29 22:28 创建
 *
 */
package com.thh.tpc.protocol.conf;

import com.thh.tpc.protocol.domain.Node;

import java.net.URL;

/**
 * @author daidai@yiji.com
 */
public class TransactionManagerNode implements Node {
    private String name;
    private URL address;

    public TransactionManagerNode(String name, URL address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public URL communicationAddr() {
        return null;
    }

}
