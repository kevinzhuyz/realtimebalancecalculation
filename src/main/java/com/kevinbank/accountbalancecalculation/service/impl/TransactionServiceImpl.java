package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.domain.Transaction;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    
    public TransactionServiceImpl(AccountMapper accountMapper, TransactionMapper transactionMapper) {
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }
    
    @Override
    @Transactional
    public Transaction transfer(String sourceCardId, String targetCardId, BigDecimal amount) {
        logger.info("Starting transfer from {} to {} with amount {}", sourceCardId, targetCardId, amount);
        
        // 参数校验
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid transfer amount: {}", amount);
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        try {
            // 获取账户信息
            Account sourceAccount = accountMapper.findById(Long.parseLong(sourceCardId));
            Account targetAccount = accountMapper.findById(Long.parseLong(targetCardId));
            
            logger.info("Source account: {}", sourceAccount);
            logger.info("Target account: {}", targetAccount);
            
            // 账户存在性检查
            if (sourceAccount == null || targetAccount == null) {
                logger.error("Account not found - Source: {}, Target: {}", sourceAccount, targetAccount);
                throw new IllegalArgumentException("Source or target account not found");
            }
            
            // 余额检查
            if (sourceAccount.getBalance().compareTo(amount) < 0) {
                logger.error("Insufficient balance. Current balance: {}, Required amount: {}", 
                        sourceAccount.getBalance(), amount);
                throw new IllegalArgumentException("Insufficient balance");
            }
            
            // 更新源账户余额
            BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
            int sourceUpdateResult = accountMapper.updateBalance(sourceAccount.getCardId(), newSourceBalance);
            logger.info("Source account update result: {}", sourceUpdateResult);
            
            // 更新目标账户余额
            BigDecimal newTargetBalance = targetAccount.getBalance().add(amount);
            int targetUpdateResult = accountMapper.updateBalance(targetAccount.getCardId(), newTargetBalance);
            logger.info("Target account update result: {}", targetUpdateResult);
            
            // 记录交易
            Transaction transaction = new Transaction();
            transaction.setTransactionId(UUID.randomUUID().toString());
            transaction.setAmount(amount);
            transaction.setSourceCardId(sourceCardId);
            transaction.setTargetCardId(targetCardId);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setTranType("TRANSFER");
            
            int transactionInsertResult = transactionMapper.insert(transaction);
            logger.info("Transaction insert result: {}", transactionInsertResult);
            
            return transaction;
            
        } catch (NumberFormatException e) {
            logger.error("Invalid card ID format", e);
            throw new IllegalArgumentException("Invalid card ID format: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Transfer failed", e);
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Transaction> getTransactionsByCardId(String cardId) {
        return transactionMapper.findByCardId(cardId);
    }
    
    @Override
    public Transaction getTransactionById(String transactionId) {
        return transactionMapper.findByTransactionId(transactionId);
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionMapper.findAll();
    }
} 