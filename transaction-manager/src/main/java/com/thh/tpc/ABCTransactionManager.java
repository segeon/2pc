/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 21:36 创建
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
@SpringBootApplication(scanBasePackages = {"com.thh.tpc"})
public class ABCTransactionManager {

    public static void main(String[] args) {
        SpringApplication.run(ABCTransactionManager.class, args);
    }
}
