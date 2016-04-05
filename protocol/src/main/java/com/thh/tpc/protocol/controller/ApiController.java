/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-27 21:39 创建
 *
 */
package com.thh.tpc.protocol.controller;

import com.thh.tpc.protocol.handler.HandlerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author daidai@yiji.com
 */

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private HandlerManager handlerManager;

    @RequestMapping(params = {"command"}, method = RequestMethod.POST)
    @ResponseBody
    String handleTransactionReq(HttpServletRequest request, @RequestParam String command, @RequestParam String data) {
        return handlerManager.handle(command, data);
    }
}
