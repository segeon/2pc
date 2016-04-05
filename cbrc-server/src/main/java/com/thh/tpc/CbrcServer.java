/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-02 16:38 创建
 *
 */
package com.thh.tpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author daidai@yiji.com
 */
@EnableAutoConfiguration
@SpringBootApplication
public class CbrcServer {
    public static void main(String[] args) {
        SpringApplication.run(CbrcServer.class, args);
    }
}
