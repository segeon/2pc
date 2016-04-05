/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-02 18:23 创建
 *
 */
package com.thh.tpc.customer;

import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.MessageSenderImpl;
import com.thh.tpc.protocol.util.TestUtil;
import com.thh.tpc.protocol.util.TransactionIdGenerator;

import java.util.*;

/**
 * @author daidai@yiji.com
 */
public class ClientMain {
    private static Map<String, Node> participants = new HashMap<>();
    private static Set<String> bankNames = new HashSet<>();
    private static MessageSenderImpl messageSender = new MessageSenderImpl();

    private static void usage() {
        System.out.println("使用方法: ");
        System.out.println("\t转出银行  转出账号  转入银行  转入账号  转账金额");
    }

    public static void main(String[] args) {
        if (args.length < 5) {
            usage();
            System.exit(1);
        }

        init();
        final String fromBank = args[0];
        final String fromAccount = args[1];
        final String toBank = args[2];
        final String toAccount = args[3];
        final String amount = args[4];

        if (!bankNames.contains(fromBank) || !bankNames.contains(toBank)) {
            System.err.println("转入银行或转出银行名称有误! 目前只支持: " + String.join(",", bankNames));
            System.exit(1);
        }

        try {
            Integer.parseInt(fromAccount);
            Integer.parseInt(toAccount);
        } catch (Exception e) {
            System.err.println("转出账号和转入账号必须为整数!");
            System.exit(1);
        }

        long amountLong = 0L;
        try {
            amountLong = Long.parseLong(amount);
        } catch (Exception e) {
            System.err.println("转账金额必须为整数!");
            System.exit(1);
        }

        final TransferAction transferAction = new TransferAction(fromBank, fromAccount, toBank, toAccount, amountLong);
        List<Action> actions = new ArrayList<>();
        actions.add(transferAction);
        final DistributedTransactionImpl transaction = new DistributedTransactionImpl(TransactionIdGenerator.getTransactionId(), actions, new LinkedList<VoteResult>(), TestUtil.getTestNodes());
        final TransReqResult transReqResult = messageSender.sendTransactionRequest2Coordinator(TestUtil.getCoordinatorNode(), transaction);
        if (transReqResult.getDecision().equals(Decision.Commit)) {
            System.out.println(String.format("交易成功! \n流水号: %s, 转出行: %s, 转出账号: %s, 转入行: %s, 转入账号: %s, 转账金额: %s",
                    transaction.id(), fromBank, fromAccount, toBank, toAccount, amount));
        } else {
            System.err.println("交易失败! \n" + transReqResult.getDescription());
        }
    }

    private static void init() {
        final List<Node> testNodes = TestUtil.getTestNodes();bankNames.add("BOC");
        bankNames.add("CCB");
        for (Node testNode : testNodes) {
            participants.put(testNode.name(), testNode);
        }
    }

}
