package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import java.util.List;

public interface TransactionService {
    
    Transaction createTransaction(CreateTransactionRequest request);
    
    List<Transaction> getTransactionsByAccountId(Long accountId);
    
    Transaction getTransactionById(Long id);
    
    List<Transaction> getAllTransactions();
} 