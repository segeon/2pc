/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 22:30 创建
 *
 */
package com.thh.tpc.bankcommon.domain;

import com.thh.tpc.bankcommon.service.*;
import com.thh.tpc.protocol.conf.TransactionManagerNode;
import com.thh.tpc.protocol.domain.*;
import com.thh.tpc.protocol.service.MessageSender;
import com.thh.tpc.protocol.service.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
public class BankingParticipant extends AbstractParticipant {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AccountOperationService accountOperationService;
    @Autowired
    private AccountFreezingRecordRepository freezingRecordRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private TransactionManagerNode transactionManagerNode;
    @Autowired
    private AccountRepository accountRepository;


    public BankingParticipant(String name, URL address) {
        super(name, address);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        initAccount();
    }

    public void initAccount() {
        if (name().equals("BOC")) {
            Account account = accountRepository.findOne(1);
            if (null == account) {
                logger.info("初始化BOC account表...");
                account = new Account();
                account.setAccountId(1);
                account.setBalance(10000L);
                account.setFrozenBalance(0L);
                account.setAddTime(new Date());
                account.setLastModifyTime(new Date());
                account.setUserId("Tom");
                accountRepository.saveAndFlush(account);
            }
        } else if (name().equals("CCB")){
            Account account2 = accountRepository.findOne(2);
            if (null == account2) {
                logger.info("初始化CCB account表...");
                account2 = new Account();
                account2.setAccountId(2);
                account2.setBalance(0L);
                account2.setFrozenBalance(0L);
                account2.setAddTime(new Date());
                account2.setLastModifyTime(new Date());
                account2.setUserId("Bob");
                accountRepository.saveAndFlush(account2);
            }
        }
    }

    private void freezeAccountsOrCheckAccounts(DistributedTransaction transaction) {
        final List<Action> actions = transaction.requestedActions();
        for (Action action : actions) {
            if (action instanceof TransferAction) {
                final TransferAction transferAction = (TransferAction) action;
                if (transferAction.getFromBank().equals(name())) {
                    final AccountFreezeRequest freezeRequest = new AccountFreezeRequest(transferAction.getFromUserName(), Integer.parseInt(transferAction.getFromAccount()), transferAction.getAmount(), transaction.id());
                    accountOperationService.freezeAccount(freezeRequest);
                } else if (transferAction.getToBank().equals(name())) {
                    final Account one = accountRepository.findOne(Integer.parseInt(transferAction.getToAccount()));
                    if (null == one) {
                        throw new IllegalArgumentException("accountId=" + transferAction.getToAccount() + "的账号不存在!");
                    }
                    if (StringUtils.hasText(transferAction.getToUserName()) && !one.getUserId().equals(transferAction.getToUserName())) {
                        throw new IllegalArgumentException("accountId=" + transferAction.getToAccount() +"的账号用户名不正确!");
                    }
                } else {
                    throw new IllegalArgumentException("收到非本行的转账请求!");
                }
            }
        }
    }

    private void check(DistributedTransaction transaction) throws IllegalArgumentException {
        final List<Action> actions = transaction.requestedActions();
        for (Action action : actions) {
            if (action instanceof TransferAction) {
                final TransferAction transferAction = (TransferAction) action;
                if (!StringUtils.hasText(transferAction.getFromBank())
                    || !StringUtils.hasText(transferAction.getToBank())
                    || !StringUtils.hasText(transferAction.getFromAccount())
                    || !StringUtils.hasText(transferAction.getToAccount())
                    || transferAction.getAmount() <= 0) {
                    throw new IllegalArgumentException("转出行, 转出账号, 转入行, 转入账号为空, 或者转账金额非法!");
                }
            }
        }
    }


    @Override
    public VoteResult vote(final DistributedTransaction transaction) {
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    getParticipantLogKeeper().saveOrUpdate(transaction);
                    check(transaction);
                    freezeAccountsOrCheckAccounts(transaction);
                    getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Yes);
                }
            });
            return new VoteResultImpl(transaction.id(), name(), System.currentTimeMillis(), Vote.Yes, "ok");
        } catch (Exception e) {
            logger.error("{} votes No for {}", name(), transaction.id(), e);
            final VoteResultImpl voteResult = new VoteResultImpl(transaction.id(), name(), System.currentTimeMillis(), Vote.No, e.getMessage());
            try {
                getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Abort);
            } catch (Exception e2) {
                logger.error("Fail to record Abort status for transaction {}", transaction.id(), e2);
            }
            return voteResult;
        }
    }

    private void deductAndDeposit(DistributedTransaction transaction) {
        final List<Action> actions = transaction.requestedActions();
        for (Action action : actions) {
            if (action instanceof TransferAction) {
                final TransferAction transferAction = (TransferAction) action;
                if (transferAction.getFromBank().equals(name())) {
                    final AccountFreezingRecord freezingRecord = freezingRecordRepository.findByTransactionId(transaction.id());
                    UnfreezeAndDeductRequest request = new UnfreezeAndDeductRequest(freezingRecord.getId());
                    accountOperationService.unfreezeAndDeduct(request);
                } else if (transferAction.getToBank().equals(name())){
                    DepositRequest depositRequest = new DepositRequest(transferAction.getToUserName(), Integer.parseInt(transferAction.getToAccount()), transferAction.getAmount(), transaction.id());
                    accountOperationService.deposit(depositRequest);
                }
            }
        }
    }

    private void unfreezeAccounts(DistributedTransaction transaction) {
        final List<Action> actions = transaction.requestedActions();
        for (Action action : actions) {
            if (action instanceof TransferAction) {
                final TransferAction transferAction = (TransferAction) action;
                if (transferAction.getFromBank().equals(name())) {
                    final AccountFreezingRecord freezingRecord = freezingRecordRepository.findByTransactionId(transaction.id());
                    AccountUnfreezeRequest request = new AccountUnfreezeRequest(freezingRecord.getId());
                    accountOperationService.unfreezeAccount(request);
                }
            }
        }
    }

    @Override
    public void commit(DistributedTransaction transaction) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Commit);
                deductAndDeposit(transaction);
            }
        });
    }

    @Override
    public void abort(DistributedTransaction transaction) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getParticipantLogKeeper().recordMsg(transaction, com.thh.tpc.protocol.domain.TransactionStatus.Abort);
                unfreezeAccounts(transaction);
            }
        });
    }

    @Override
    public Decision resolveDecisionByCTP(DistributedTransaction transaction) {
        final List<Node> participants = transaction.participants();
        final DecisionQueryMessage decisionQueryMessage = new DecisionQueryMessage();
        decisionQueryMessage.setTransactionId(transaction.id());
        for (Node participant : participants) {
            final DecisionQueryResult result = messageSender.sendDecisionQuery2Others(participant, decisionQueryMessage);
            if (!result.getDecision().equals(Decision.Unknown)) {
                return result.getDecision();
            }
        }
        final DecisionQueryResult result = messageSender.sendDecisionQuery2Others(transactionManagerNode, decisionQueryMessage);
        return result.getDecision();
    }

}
