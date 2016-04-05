/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 15:18 创建
 *
 */
package com.thh.tpc.protocol.domain;

import com.google.common.base.Objects;

import java.net.URL;

/**
 * @author daidai@yiji.com
 */
public class NodeImpl implements Node {
    private String name;
    private URL address;

    public NodeImpl() {
    }

    public NodeImpl(String name, URL address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getAddress() {
        return address;
    }

    public void setAddress(URL address) {
        this.address = address;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public URL communicationAddr() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeImpl node = (NodeImpl) o;
        return Objects.equal(name, node.name) &&
                Objects.equal(address, node.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, address);
    }
}
