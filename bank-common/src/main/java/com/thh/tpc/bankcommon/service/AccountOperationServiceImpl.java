/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:14 创建
 *
 */
package com.thh.tpc.bankcommon.service;

import com.thh.tpc.bankcommon.domain.Account;
import com.thh.tpc.bankcommon.domain.AccountFreezingRecord;
import com.thh.tpc.protocol.domain.TransactionEntity;
import com.thh.tpc.protocol.service.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author daidai@yiji.com
 */
@Service
public class AccountOperationServiceImpl implements AccountOperationService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ACCOUNT_NOT_EXIST_MSG = "该账号不存在: ";
    private static final String ACCOUNT_USERNAME_NOT_MATCH_MSG = "账号和用户名不匹配: ";
    private static final String TRANSACTION_NOT_EXIST_MSG = "transactionId不存在: ";
    private static final String BALANCE_NOT_ENOUGH_MSG = "该账号可用余额不足!";
    private static final String FREEZE_RECORD_NOT_EXIST_MSG = "冻结记录不存在: ";
    private static final String ALREADY_UNFROZEN_MSG = "该记录已经被解冻: ";
    private static final String INVALID_UNFREEZE_AMOUNT_MSG = "账号冻结金额小于请求解冻金额!";
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountFreezingRecordRepository freezingRecordRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void freezeAccount(AccountFreezeRequest request) throws FreezeAccountException {
        logger.info("收到冻结请求: {}", request.toString());
        final Account account = accountRepository.findOne(request.getAccountId());
        if (account == null) {
            final String message = ACCOUNT_NOT_EXIST_MSG + request.getAccountId();
            logger.error(message);
            throw new FreezeAccountException(message);
        }
        if (StringUtils.hasText(request.getUserName()) && !account.getUserId().equals(request.getUserName())) {
            final String message = ACCOUNT_USERNAME_NOT_MATCH_MSG + request.getAccountId();
            logger.error(message);
            throw new FreezeAccountException(message);
        }
        final TransactionEntity transactionEntity = transactionRepository.findOne(request.getTransactionId());
        if (transactionEntity == null) {
            final String message = TRANSACTION_NOT_EXIST_MSG + request.getTransactionId();
            logger.error(message);
            throw new FreezeAccountException(message);
        }
        long useableBalance = account.getBalance() - account.getFrozenBalance();
        if (useableBalance < request.getFreezeAmount()) {
            final String message = BALANCE_NOT_ENOUGH_MSG;
            logger.error(message);
            throw new FreezeAccountException(message);
        }
        account.setFrozenBalance(account.getFrozenBalance() + request.getFreezeAmount());
        account.setLastModifyTime(new Date());
        final AccountFreezingRecord freezingRecord = new AccountFreezingRecord();
        freezingRecord.setAccountId(request.getAccountId());
        freezingRecord.setFrozenAmount(request.getFreezeAmount());
        freezingRecord.setTransactionId(request.getTransactionId());
        freezingRecord.setFreezingTime(new Date());
        freezingRecord.setValid(true);
        accountRepository.save(account);
        freezingRecordRepository.save(freezingRecord);
        logger.info("冻结成功: {}", request.toString());
    }

    @Override
    public void unfreezeAccount(AccountUnfreezeRequest request) throws UnfreezeAccountException {
        logger.info("收到解冻请求: {}", request.toString());
        final AccountFreezingRecord freezingRecord = freezingRecordRepository.findOne(request.getFreezeRecordId());
        if (freezingRecord == null) {
            final String message = FREEZE_RECORD_NOT_EXIST_MSG + request.getFreezeRecordId();
            logger.error(message);
            throw new UnfreezeAccountException(message);
        }
        final Account account = accountRepository.findOne(freezingRecord.getAccountId());
        if (account == null) {
            final String message = ACCOUNT_NOT_EXIST_MSG + freezingRecord.getAccountId();
            logger.error(message);
            throw new UnfreezeAccountException(message);
        }
        final TransactionEntity transactionEntity = transactionRepository.findOne(freezingRecord.getTransactionId());
        if (transactionEntity == null) {
            final String message = TRANSACTION_NOT_EXIST_MSG + freezingRecord.getTransactionId();
            logger.error(message);
            throw new UnfreezeAccountException(message);
        }
        if (!freezingRecord.isValid()) {
            throw new UnfreezeAccountException(ALREADY_UNFROZEN_MSG + freezingRecord.getId());
        }
        if (account.getFrozenBalance() < freezingRecord.getFrozenAmount()) {
            final String message = INVALID_UNFREEZE_AMOUNT_MSG;
            logger.error(message);
            throw new UnfreezeAccountException(message);
        }
        account.setFrozenBalance(account.getFrozenBalance() - freezingRecord.getFrozenAmount());
        account.setLastModifyTime(new Date());
        freezingRecord.setUnfreezingTime(new Date());
        freezingRecord.setValid(false);
        accountRepository.save(account);
        freezingRecordRepository.save(freezingRecord);
        logger.info("解冻成功: {}", request.toString());
    }

    @Override
    public void deposit(DepositRequest request) throws DepositException {
        logger.info("收到入款请求: {}", request.toString());
        final Account account = accountRepository.findOne(request.getAccountId());
        if (account == null) {
            final String message = ACCOUNT_NOT_EXIST_MSG + request.getAccountId();
            logger.error(message);
            throw new DepositException(message);
        }
        if (StringUtils.hasText(request.getUserName()) && !account.getUserId().equals(request.getUserName())) {
            String message = ACCOUNT_USERNAME_NOT_MATCH_MSG + request.getAccountId();
            logger.error(message);
            throw new DepositException(message);
        }
        final TransactionEntity transactionEntity = transactionRepository.findOne(request.getTransactionId());
        if (transactionEntity == null) {
            final String message = TRANSACTION_NOT_EXIST_MSG + request.getTransactionId();
            logger.error(message);
            throw new DepositException(message);
        }
        account.setBalance(account.getBalance() + request.getAmount());
        account.setLastModifyTime(new Date());
        accountRepository.save(account);
        logger.info("入款成功: {}", request.toString());
    }

    @Override
    public void deduct(DeductRequest request) throws DeductException {
        logger.info("收到扣款请求: {}", request.toString());
        final Account account = accountRepository.findOne(request.getAccountId());
        if (account == null) {
            final String message = ACCOUNT_NOT_EXIST_MSG + request.getAccountId();
            logger.error(message);
            throw new DeductException(message);
        }
        if (StringUtils.hasText(request.getUserName()) && !account.getUserId().equals(request.getUserName())) {
            String message = ACCOUNT_USERNAME_NOT_MATCH_MSG + request.getAccountId();
            logger.error(message);
            throw new DepositException(message);
        }
        final TransactionEntity transactionEntity = transactionRepository.findOne(request.getTransactionId());
        if (transactionEntity == null) {
            final String message = TRANSACTION_NOT_EXIST_MSG + request.getTransactionId();
            logger.error(message);
            throw new DeductException(message);
        }
        if (request.getAmount() > account.getBalance() - account.getFrozenBalance()) {
            final String message = BALANCE_NOT_ENOUGH_MSG;
            logger.error(message);
            throw new DeductException(message);
        }
        account.setBalance(account.getBalance() - request.getAmount());
        account.setLastModifyTime(new Date());
        accountRepository.save(account);
        logger.info("扣款成功: ", request.toString());
    }

    @Override
    public void unfreezeAndDeduct(UnfreezeAndDeductRequest request) throws UnfreezeAndDeductException {
        logger.info("收到解冻扣款请求: ", request.toString());
        final AccountFreezingRecord freezingRecord = freezingRecordRepository.findOne(request.getFreezeRecordId());
        if (freezingRecord == null) {
            final String message = FREEZE_RECORD_NOT_EXIST_MSG + request.getFreezeRecordId();
            logger.error(message);
            throw new UnfreezeAndDeductException(message);
        }
        final Account account = accountRepository.findOne(freezingRecord.getAccountId());
        if (account == null) {
            final String message = ACCOUNT_NOT_EXIST_MSG + freezingRecord.getAccountId();
            logger.error(message);
            throw new UnfreezeAndDeductException(message);
        }
        final TransactionEntity transactionEntity = transactionRepository.findOne(freezingRecord.getTransactionId());
        if (transactionEntity == null) {
            final String message = TRANSACTION_NOT_EXIST_MSG + freezingRecord.getTransactionId();
            logger.error(message);
            throw new UnfreezeAndDeductException(message);
        }
        if (!freezingRecord.isValid()) {
            final String message = ALREADY_UNFROZEN_MSG + freezingRecord.getId();
            logger.error(message);
            throw new UnfreezeAndDeductException(message);
        }
        if (account.getFrozenBalance() < freezingRecord.getFrozenAmount()) {
            final String message = INVALID_UNFREEZE_AMOUNT_MSG;
            logger.error(message);
            throw new UnfreezeAndDeductException(message);
        }
        if (account.getBalance() < account.getFrozenBalance()) {
            final String message = BALANCE_NOT_ENOUGH_MSG;
            logger.error(message);
            throw new UnfreezeAndDeductException(message);
        }
        account.setFrozenBalance(account.getFrozenBalance() - freezingRecord.getFrozenAmount());
        account.setBalance(account.getBalance() - freezingRecord.getFrozenAmount());
        account.setLastModifyTime(new Date());
        freezingRecord.setUnfreezingTime(new Date());
        freezingRecord.setValid(false);
        accountRepository.save(account);
        freezingRecordRepository.save(freezingRecord);
        logger.info("解冻扣款成功: ", request.toString());
    }
}
