package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    
    Account createAccount(CreateAccountRequest request);
    
    void updateBalance(Long accountId, BigDecimal amount);
    
    Account getAccountById(Long id);
    
    List<Account> getAllAccounts();
    
    Account deposit(Long accountId, BigDecimal amount);
    
    Account withdraw(Long accountId, BigDecimal amount);
    
    void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount);
} 