/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:00 创建
 *
 */
package com.thh.tpc.bankcommon.service;

/**
 * @author daidai@yiji.com
 */

public interface AccountOperationService {

    void freezeAccount(AccountFreezeRequest request) throws FreezeAccountException;

    void unfreezeAccount(AccountUnfreezeRequest request) throws UnfreezeAccountException;

    void deposit(DepositRequest request) throws DepositException;

    void deduct(DeductRequest request) throws DeductException;

    void unfreezeAndDeduct(UnfreezeAndDeductRequest request) throws UnfreezeAndDeductException;
}
