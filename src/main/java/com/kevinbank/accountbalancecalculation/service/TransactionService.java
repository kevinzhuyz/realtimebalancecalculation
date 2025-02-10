package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import java.util.List;

/**
 * 交易服务接口，提供与交易相关的操作
 */
public interface TransactionService {

    /**
     * 创建交易记录
     * @param request 交易请求
     * @return 交易记录
     */
    Transaction createTransaction(CreateTransactionRequest request);

    /**
     * 获取指定账户的所有交易记录
     * @param accountId 账户ID
     * @return 交易记录列表
     */
    List<Transaction> getTransactionsByAccountId(Long accountId);

    /**
     * 根据ID获取交易记录
     * @param id 交易记录ID
     * @return 交易记录
     */
    Transaction getTransactionById(Long id);

    /**
     * 获取所有交易记录
     * @return 交易记录列表
     */
    List<Transaction> getAllTransactions();
}
