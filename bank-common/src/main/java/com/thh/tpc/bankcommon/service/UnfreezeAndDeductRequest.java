/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-28 23:32 创建
 *
 */
package com.thh.tpc.bankcommon.service;

/**
 * @author daidai@yiji.com
 */
public class UnfreezeAndDeductRequest {
    private int freezeRecordId;

    public UnfreezeAndDeductRequest(int freezeRecordId) {
        this.freezeRecordId = freezeRecordId;
    }

    public int getFreezeRecordId() {
        return freezeRecordId;
    }

    public void setFreezeRecordId(int freezeRecordId) {
        this.freezeRecordId = freezeRecordId;
    }

    @Override
    public String toString() {
        return "UnfreezeAndDeductRequest{" +
                "freezeRecordId=" + freezeRecordId +
                '}';
    }
}
