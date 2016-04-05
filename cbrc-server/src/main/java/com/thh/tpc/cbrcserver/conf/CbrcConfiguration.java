/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-02 16:16 创建
 *
 */
package com.thh.tpc.cbrcserver.conf;

import com.thh.tpc.cbrcserver.domain.CbrcParticipant;
import com.thh.tpc.protocol.conf.TransactionManagerNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author daidai@yiji.com
 */
@Configuration
public class CbrcConfiguration {
    @Bean
    public TransactionManagerNode transactionManagerNode() {
        try {
            return new TransactionManagerNode("transactionManager", new URL("http", "127.0.0.1", 8000, "/api"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Bean
    public CbrcParticipant bankingParticipant() {
        try {
            return new CbrcParticipant("CBRC", new URL("http", "127.0.0.1", 8003, "/api"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
