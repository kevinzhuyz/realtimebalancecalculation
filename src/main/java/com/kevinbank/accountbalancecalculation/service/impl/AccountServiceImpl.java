package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.domain.Transaction;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    
    public AccountServiceImpl(AccountMapper accountMapper, TransactionMapper transactionMapper) {
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }
    
    @Override
    public Account getAccount(Long cardId) {
        return accountMapper.findById(cardId);
    }
    
    @Override
    public List<Account> getAllAccounts() {
        return accountMapper.findAll();
    }
    
    @Override
    public Account createAccount(Account account) {
        accountMapper.insert(account);
        return account;
    }
    
    @Override
    @Transactional
    public boolean transfer(String sourceCardId, String targetCardId, BigDecimal amount) {
        Account sourceAccount = accountMapper.findById(Long.parseLong(sourceCardId));
        Account targetAccount = accountMapper.findById(Long.parseLong(targetCardId));
        
        if (sourceAccount == null || targetAccount == null) {
            return false;
        }
        
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            return false;
        }
        
        // Update balances
        accountMapper.updateBalance(sourceAccount.getCardId(), sourceAccount.getBalance().subtract(amount));
        accountMapper.updateBalance(targetAccount.getCardId(), targetAccount.getBalance().add(amount));
        
        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(amount);
        transaction.setSourceCardId(sourceCardId);
        transaction.setTargetCardId(targetCardId);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTranType("TRANSFER");
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setTargetAccount(targetAccount.getAccountNumber());
        
        transactionMapper.insert(transaction);
        
        return true;
    }
    
    @Override
    public List<Transaction> getTransactionHistory(String cardId) {
        return transactionMapper.findByCardId(cardId);
    }
    
    @Override
    @Transactional
    public Account deposit(String cardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Account account = accountMapper.findById(Long.parseLong(cardId));
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        // 更新账户余额
        BigDecimal newBalance = account.getBalance().add(amount);
        accountMapper.updateBalance(account.getCardId(), newBalance);
        
        // 记录交易
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(amount);
        transaction.setTargetCardId(cardId);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTranType("DEPOSIT");
        
        transactionMapper.insert(transaction);
        
        account.setBalance(newBalance);
        return account;
    }
    
    @Override
    @Transactional
    public Account withdraw(String cardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Account account = accountMapper.findById(Long.parseLong(cardId));
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        // 检查余额是否充足
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        // 更新账户余额
        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountMapper.updateBalance(account.getCardId(), newBalance);
        
        // 记录交易
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(amount);
        transaction.setSourceCardId(cardId);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTranType("WITHDRAW");
        
        transactionMapper.insert(transaction);
        
        account.setBalance(newBalance);
        return account;
    }
} 