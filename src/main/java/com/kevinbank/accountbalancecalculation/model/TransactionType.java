package com.kevinbank.accountbalancecalculation.model;

/**
 * 交易类型枚举
 */
public enum TransactionType {
    /**
     * 存款
     */
    DEPOSIT,
    
    /**
     * 取款
     */
    WITHDRAW,
    
    /**
     * 转账
     */
    TRANSFER,
    
    /**
     * 支付
     */
    PAYMENT,
    
    /**
     * 退款
     */
    REFUND;
    
    /**
     * 判断是否为支出类型
     */
    public boolean isDebit() {
        return this == WITHDRAW || this == TRANSFER || this == PAYMENT;
    }
    
    /**
     * 判断是否为收入类型
     */
    public boolean isCredit() {
        return this == DEPOSIT || this == REFUND;
    }
} 