/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 22:42 创建
 *
 */
package com.thh.tpc.protocol.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author daidai@yiji.com
 */
@Component
public class HandlerManager implements ApplicationContextAware, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext context;
    private ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


    public String handle(String command, String data) {
        final Handler handler = handlerMap.get(command);
        if (handler == null) {
            logger.error("不支持的command:" + command);
            throw new IllegalArgumentException("不支持的command:" + command);
        }
        return handler.handle(data);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final String[] beans = context.getBeanNamesForType(Handler.class);
        for (String bean : beans) {
            final Handler handler = (Handler)context.getBean(bean);
            if (handler.getClass().equals(HandlerManager.class)) {
                continue;
            }
            handlerMap.putIfAbsent(handler.command(), handler);
        }
    }
}
