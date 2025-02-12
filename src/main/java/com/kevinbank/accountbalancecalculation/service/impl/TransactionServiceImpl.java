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

/**
 * 交易服务实现类
 */
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
    private static final String TRANSACTION_CACHE_KEY_PREFIX = "transaction:";
    private static final long CACHE_TIMEOUT = 30; // 缓存30分钟

    /**
     * 创建交易记录
     *
     * @param request 创建交易记录的请求对象
     * @return 保存后的交易记录对象
     * @throws RuntimeException 如果操作太频繁或交易处理失败
     */
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

                // 保存到缓存
                String cacheKey = TRANSACTION_CACHE_KEY_PREFIX + savedTransaction.getId();
                log.info("Attempting to cache transaction with key: {}", cacheKey);
                try {
                    cacheService.set(cacheKey, savedTransaction, CACHE_TIMEOUT, TimeUnit.MINUTES);
                    // 验证缓存是否成功
                    Transaction cachedTransaction = cacheService.get(cacheKey, Transaction.class);
                    if (cachedTransaction != null) {
                        log.info("Successfully verified cache write for key: {}", cacheKey);
                    } else {
                        log.warn("Cache verification failed for key: {}", cacheKey);
                    }
                } catch (Exception e) {
                    log.error("Failed to cache transaction: {}", e.getMessage(), e);
                }

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

    /**
     * 验证交易请求的合法性
     *
     * @param request 交易请求对象
     * @throws RuntimeException 如果账户ID为空或账户不存在
     */
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

    /**
     * 验证账户是否存在
     *
     * @param accountId 账户ID
     * @throws RuntimeException 如果账户不存在
     */
    private void validateAccount(Long accountId) {
        if (!accountRepository.findById(accountId).isPresent()) {
            throw new RuntimeException("账户不存在: " + accountId);
        }
    }

    /**
     * 根据账户ID获取交易记录列表
     *
     * @param accountId 账户ID
     * @return 交易记录列表
     */
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

    /**
     * 根据ID获取交易记录
     *
     * @param id 交易记录ID
     * @return 交易记录对象
     * @throws RuntimeException 如果交易记录不存在
     */
    @Override
    public Transaction getTransactionById(Long id) {
        String cacheKey = TRANSACTION_CACHE_KEY_PREFIX + id;
        // 先从缓存获取
        Transaction transaction = cacheService.get(cacheKey, Transaction.class);
        if (transaction != null) {
            log.info("Transaction found in cache: {}", cacheKey);
            return transaction;
        }
        
        // 缓存未命中，从数据库获取
        log.info("Transaction not found in cache, fetching from database: {}", id);
        transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
            
        // 放入缓存
        cacheService.set(cacheKey, transaction, CACHE_TIMEOUT, TimeUnit.MINUTES);
        return transaction;
    }

    /**
     * 获取所有交易记录
     *
     * @return 交易记录列表
     */
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
