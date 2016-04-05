/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 21:30 创建
 *
 */
package com.thh.tpc.transactionmanager.conf;

import com.thh.tpc.protocol.domain.DefaultCoordinator;
import com.thh.tpc.protocol.domain.Node;
import com.thh.tpc.protocol.domain.NodeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author daidai@yiji.com
 */
@Configuration
public class TransactionManagerConfiguration {

    @Bean
    public DefaultCoordinator defaultCoordinator() {
        final ArrayList<Node> nodes = new ArrayList<>(3);
        try {
            nodes.add(new NodeImpl("BOC", new URL("http", "127.0.0.1", 8001, "/api")));
            nodes.add(new NodeImpl("CCB", new URL("http", "127.0.0.1", 8002, "/api")));
            nodes.add(new NodeImpl("CBRC", new URL("http", "127.0.0.1", 8003, "/api")));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        try {
            final DefaultCoordinator defaultCoordinator = new DefaultCoordinator("transactionManager", nodes, new URL("http", "127.0.0.1", 8000, "/api"));
            return defaultCoordinator;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
