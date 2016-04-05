/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-30 20:51 创建
 *
 */
package com.thh.tpc.protocol.util;

import com.thh.tpc.protocol.domain.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
public class TestUtil {

    private static Node coordinatorNode;
    private static Node bocNode;
    private static Node ccbNode;
    private static Node cbrcNode;

    private static List<Node> testNodes;

    static {
        try {
            bocNode = new NodeImpl("BOC", new URL("http", "127.0.0.1", 8001, "/api"));
            ccbNode = new NodeImpl("CCB", new URL("http", "127.0.0.1", 8002, "/api"));
            cbrcNode = new NodeImpl("CBRC", new URL("http", "127.0.0.1", 8003, "/api"));
            testNodes = Arrays.asList(bocNode, ccbNode, cbrcNode);
            coordinatorNode = new NodeImpl("transactionManager", new URL("http", "127.0.0.1", 8000, "/api"));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Node getBocNode() {
        return bocNode;
    }

    public static Node getCcbNode() {
        return ccbNode;
    }

    public static Node getCbrcNode() {
        return cbrcNode;
    }

    public static List<Node> getTestNodes() {
        return testNodes;
    }

    public static DistributedTransactionImpl getTransaction() {
        final ArrayList<Action> actions = new ArrayList<>();
        final TransferAction transferAction = new TransferAction("BOC", "1", "CCB", "2", 100L);
        //transferAction.setParticipantNames(Arrays.asList("BOC", "CCB", "CBRC"));
        actions.add(transferAction);
        final String transactionId = TransactionIdGenerator.getTransactionId();
        final DistributedTransactionImpl distributedTransaction = new DistributedTransactionImpl(transactionId, actions,
                new ArrayList<>(), TestUtil.getTestNodes());
        return distributedTransaction;
    }

    public static DistributedTransactionImpl getWouldAbortTransaction() {
        final ArrayList<Action> actions = new ArrayList<>();
        final TransferAction transferAction = new TransferAction("BOC", "1", "CCB",  "2", 10000L);
        //transferAction.setParticipantNames(Arrays.asList("BOC", "CCB", "CBRC"));
        actions.add(transferAction);
        final String transactionId = TransactionIdGenerator.getTransactionId();
        final DistributedTransactionImpl distributedTransaction = new DistributedTransactionImpl(transactionId, actions,
                new ArrayList<>(), TestUtil.getTestNodes());
        return distributedTransaction;
    }

    public static Node getCoordinatorNode() {
        return coordinatorNode;
    }
}
