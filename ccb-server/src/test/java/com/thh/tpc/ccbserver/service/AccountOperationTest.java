package com.thh.tpc.ccbserver.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import com.thh.tpc.protocol.service.TransactionService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.thh.tpc.bankcommon.domain.Account;
import com.thh.tpc.bankcommon.service.*;

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
        final Account account2 = new Account();
        account2.setAccountId(2);
        account2.setBalance(0L);
        account2.setFrozenBalance(0L);
        account2.setAddTime(new Date());
        account2.setLastModifyTime(new Date());
        account2.setUserId("Bob");
        accountRepository.save(account2);
    }


}