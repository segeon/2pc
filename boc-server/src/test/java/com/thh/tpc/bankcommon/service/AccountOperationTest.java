package com.thh.tpc.bankcommon.service;

import com.thh.tpc.TestBase;
import com.thh.tpc.bankcommon.domain.Account;
import com.thh.tpc.bankcommon.domain.AccountFreezingRecord;
import com.thh.tpc.protocol.domain.DistributedTransactionImpl;
import com.thh.tpc.protocol.service.TransactionService;
import com.thh.tpc.protocol.util.TestUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author daidai@yiji.com
 */
public class AccountOperationTest extends TestBase{
    private static final String TRANSACTION_ID = "71227e1809d8404882e257049918ca79";
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountOperationService accountOperationService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountFreezingRecordRepository freezingRecordRepository;

    @Test

    public void testCreateAccount() {
        final Account account = new Account();
        account.setAccountId(1);
        account.setBalance(10000L);
        account.setFrozenBalance(3000L);
        account.setAddTime(new Date());
        account.setLastModifyTime(new Date());
        account.setUserId("Tom");
        accountRepository.save(account);
        /*final Account account2 = new Account();
        account2.setAccountId(2);
        account2.setBalance(0L);
        account2.setFrozenBalance(0L);
        account2.setAddTime(new Date());
        account2.setLastModifyTime(new Date());
        account2.setUserId("Bob");
        accountRepository.save(account2);*/
    }

    @Test
    @Ignore
    public void testCreateTransaction() {
        final DistributedTransactionImpl transaction = TestUtil.getTransaction();
        System.out.println("TransactionId=" + transaction.getId());
        transactionService.saveOrUpdate(transaction);
    }

    @Test
    public void testFindAccount() throws Exception {
        final Account one = accountRepository.findOne(1);
        assertThat(one).isNotNull();
    }

    @Test
    @Transactional
    public void testDeleteFreezingRecord() throws Exception {
        AccountFreezingRecord freezingRecord = freezingRecordRepository.findByTransactionId(TRANSACTION_ID);
        if (freezingRecord != null) {
            freezingRecordRepository.deleteByTransactionId(freezingRecord.getTransactionId());
            freezingRecord = freezingRecordRepository.findByTransactionId(TRANSACTION_ID);
            assertThat(freezingRecord).isNull();
        }
    }

    @Test
    @Transactional
    public void testFreezingRecord() throws Exception {
        final Account account = accountRepository.findOne(1);
        account.setFrozenBalance(2000);
        long originalBalance = account.getBalance();
        long originalFrozon = account.getFrozenBalance();
        accountRepository.saveAndFlush(account);
        freezingRecordRepository.deleteByTransactionId(TRANSACTION_ID);

        final AccountFreezeRequest freezeRequest = new AccountFreezeRequest(null, 1, 1000, TRANSACTION_ID);
        accountOperationService.freezeAccount(freezeRequest);
        // 测试冻结记录是否正确
        final AccountFreezingRecord freezingRecord = freezingRecordRepository.findByTransactionId(TRANSACTION_ID);
        assertThat(freezingRecord).isNotNull();
        assertThat(freezingRecord.getFrozenAmount()).isEqualTo(1000);
        assertThat(freezingRecord.isValid()).isTrue();
        // 测试冻结后冻结金额和余额是否正确
        final Account afterFreezeAccount = accountRepository.findOne(1);
        assertThat(afterFreezeAccount.getFrozenBalance()).isEqualTo(originalFrozon + 1000);
        assertThat(afterFreezeAccount.getBalance()).isEqualTo(originalBalance);
        // 解冻
        final AccountUnfreezeRequest request = new AccountUnfreezeRequest(freezingRecord.getId());
        accountOperationService.unfreezeAccount(request);
        // 测试解冻后冻结金额和余额是否正确
        final Account afterUnfreeze = accountRepository.findOne(freezingRecord.getAccountId());
        assertThat(afterUnfreeze.getBalance()).isEqualTo(originalBalance);
        assertThat(afterUnfreeze.getFrozenBalance()).isEqualTo(originalFrozon);
        // 测试冻结后冻结记录状态是否正确
        final AccountFreezingRecord one = freezingRecordRepository.findOne(freezingRecord.getId());
        assertThat(one.isValid()).isFalse();
    }

    @Test
    @Transactional
    public void testDepositAndDeduct() throws Exception {
        Account account = accountRepository.findOne(1);
        long originalBalance = account.getBalance();
        long originalFrozenBalance = account.getFrozenBalance();
        // 测试存钱
        final DepositRequest depositRequest = new DepositRequest(null, account.getAccountId(), 1000, TRANSACTION_ID);
        accountOperationService.deposit(depositRequest);
        account = accountRepository.findOne(1);
        assertThat(account.getBalance()).isEqualTo(originalBalance + 1000);
        assertThat(account.getFrozenBalance()).isEqualTo(originalFrozenBalance);
        // 测试扣钱
        final DeductRequest deductRequest = new DeductRequest();
        deductRequest.setAccountId(account.getAccountId());
        deductRequest.setAmount(1000);
        deductRequest.setTransactionId(TRANSACTION_ID);
        accountOperationService.deduct(deductRequest);
        account = accountRepository.findOne(1);
        assertThat(account.getBalance()).isEqualTo(originalBalance);
        assertThat(account.getFrozenBalance()).isEqualTo(originalFrozenBalance);
    }

    @Test
    @Transactional
    public void testTransfer() {
        Account one = accountRepository.findOne(1);
        long originalBalance = one.getBalance();
        long originalFrozenBalance = one.getFrozenBalance();

        final AccountFreezeRequest freezeRequest = new AccountFreezeRequest(null, 1, 2000, TRANSACTION_ID);
        accountOperationService.freezeAccount(freezeRequest);

        final AccountFreezingRecord freezingRecord = freezingRecordRepository.findByTransactionId(TRANSACTION_ID);

        final UnfreezeAndDeductRequest request = new UnfreezeAndDeductRequest(freezingRecord.getId());
        accountOperationService.unfreezeAndDeduct(request);

        final DepositRequest depositRequest = new DepositRequest(null, 2, 2000, TRANSACTION_ID);
        accountOperationService.deposit(depositRequest);

        final Account accountOne = accountRepository.findOne(1);
        final Account accountTwo = accountRepository.findOne(2);
        final AccountFreezingRecord record = freezingRecordRepository.findByTransactionId(TRANSACTION_ID);

        assertThat(accountOne.getBalance()).isEqualTo(originalBalance - 2000);
        assertThat(accountOne.getFrozenBalance()).isEqualTo(originalFrozenBalance);
        assertThat(accountTwo.getBalance()).isEqualTo(2000);
        assertThat(record.isValid()).isFalse();
    }
}