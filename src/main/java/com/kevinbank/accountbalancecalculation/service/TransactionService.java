package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.domain.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction transfer(String sourceCardId, String targetCardId, BigDecimal amount);
    List<Transaction> getTransactionsByCardId(String cardId);
    Transaction getTransactionById(String transactionId);
    List<Transaction> getAllTransactions();
} 