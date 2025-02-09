package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.repository.TransactionRepository;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import com.kevinbank.accountbalancecalculation.service.CacheService;
import com.kevinbank.accountbalancecalculation.service.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionMapper transactionMapper;
    
    @Autowired
    private BalanceService balanceService;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String TRANSACTION_LIST_KEY = "transactions:account:";
    private static final String LOCK_KEY_PREFIX = "lock:transaction:";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transaction createTransaction(CreateTransactionRequest request) {
        log.info("开始创建交易记录: {}", request);
        
        String lockKey = LOCK_KEY_PREFIX;
        if (request.getSourceAccountId() != null) {
            lockKey += request.getSourceAccountId();
        }
        if (request.getTargetAccountId() != null) {
            lockKey += "_" + request.getTargetAccountId();
        }
        
        boolean locked = false;
        try {
            // 获取分布式锁
            locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("操作太频繁，请稍后重试");
            }
            
            // 验证请求
            validateTransactionRequest(request);
            
            // 创建交易记录
            Transaction transaction = new Transaction();
            transaction.setSourceAccountId(request.getSourceAccountId());
            transaction.setTargetAccountId(request.getTargetAccountId());
            transaction.setAmount(request.getAmount());
            transaction.setType(request.getType());
            transaction.setDescription(request.getDescription());
            transaction.setTransactionTime(LocalDateTime.now());
            
            // 保存交易记录
            log.info("保存交易记录到数据库: {}", transaction);
            transaction = transactionRepository.save(transaction);
            log.info("交易记录保存成功，ID: {}", transaction.getId());
            
            // 更新账户余额
            try {
                switch (request.getType()) {
                    case TRANSFER:
                        log.info("处理转账交易 - 源账户: {}, 目标账户: {}, 金额: {}", 
                                request.getSourceAccountId(), 
                                request.getTargetAccountId(), 
                                request.getAmount());
                                
                        if (request.getSourceAccountId() != null) {
                            balanceService.updateBalance(request.getSourceAccountId(), request.getAmount().negate());
                        }
                        if (request.getTargetAccountId() != null) {
                            balanceService.updateBalance(request.getTargetAccountId(), request.getAmount());
                        }
                        break;
                        
                    case DEPOSIT:
                        log.info("处理存款交易 - 账户: {}, 金额: {}", 
                                request.getTargetAccountId(), 
                                request.getAmount());
                                
                        if (request.getTargetAccountId() != null) {
                            balanceService.updateBalance(request.getTargetAccountId(), request.getAmount());
                        }
                        break;
                        
                    case WITHDRAW:
                        log.info("处理取款交易 - 账户: {}, 金额: {}", 
                                request.getSourceAccountId(), 
                                request.getAmount());
                                
                        if (request.getSourceAccountId() != null) {
                            balanceService.updateBalance(request.getSourceAccountId(), request.getAmount().negate());
                        }
                        break;
                }
                
                // 确认交易记录已保存
                Transaction savedTransaction = transactionRepository.findById(transaction.getId())
                        .orElseThrow(() -> new RuntimeException("无法获取已保存的交易记录"));
                log.info("交易记录已确认保存: {}", savedTransaction);
                
                return savedTransaction;
            } catch (Exception e) {
                log.error("交易处理失败: {}", e.getMessage(), e);
                throw new RuntimeException("交易处理失败: " + e.getMessage());
            }
        } finally {
            if (locked) {
                redisTemplate.delete(lockKey);
            }
        }
    }
    
    private void validateTransactionRequest(CreateTransactionRequest request) {
        switch (request.getType()) {
            case DEPOSIT:
                if (request.getTargetAccountId() == null) {
                    throw new RuntimeException("存款账户ID不能为空");
                }
                validateAccount(request.getTargetAccountId());
                break;
            case WITHDRAW:
                if (request.getSourceAccountId() == null) {
                    throw new RuntimeException("取款账户ID不能为空");
                }
                validateAccount(request.getSourceAccountId());
                break;
            case TRANSFER:
                if (request.getSourceAccountId() == null || request.getTargetAccountId() == null) {
                    throw new RuntimeException("转账的源账户和目标账户ID不能为空");
                }
                validateAccount(request.getSourceAccountId());
                validateAccount(request.getTargetAccountId());
                break;
        }
    }
    
    private void validateAccount(Long accountId) {
        if (!accountRepository.findById(accountId).isPresent()) {
            throw new RuntimeException("账户不存在: " + accountId);
        }
    }
    
    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        String cacheKey = TRANSACTION_LIST_KEY + accountId;
        
        // 先从缓存获取
        List<Transaction> transactions = cacheService.get(cacheKey, List.class);
        if (transactions != null) {
            return transactions;
        }
        
        // 缓存未命中，从数据库查询
        transactions = transactionRepository
            .findBySourceAccountIdOrTargetAccountId(accountId, accountId);
            
        // 放入缓存
        cacheService.set(cacheKey, transactions, 5, TimeUnit.MINUTES);
        
        return transactions;
    }
    
    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
} 