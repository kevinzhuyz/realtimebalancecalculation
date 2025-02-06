package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.domain.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account getAccount(Long cardId);
    List<Account> getAllAccounts();
    Account createAccount(Account account);
    boolean transfer(String sourceCardId, String targetCardId, BigDecimal amount);
    List<Transaction> getTransactionHistory(String cardId);
    Account deposit(String cardId, BigDecimal amount);
    Account withdraw(String cardId, BigDecimal amount);
} 