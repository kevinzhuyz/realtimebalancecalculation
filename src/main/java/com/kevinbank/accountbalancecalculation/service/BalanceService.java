package com.kevinbank.accountbalancecalculation.service;

import java.math.BigDecimal;

/**
 * 账户余额服务接口，用于定义更新账户余额的方法。
 */
public interface BalanceService {
    /**
     * 更新指定账户的余额。
     *
     * @param accountId 账户ID，标识需要更新余额的账户。
     * @param amount    更新金额，表示要增加或减少的金额。
     */
    void updateBalance(Long accountId, BigDecimal amount);
}
