package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.model.TransactionType;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import com.kevinbank.accountbalancecalculation.service.CacheService;
import com.kevinbank.accountbalancecalculation.service.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;
import java.util.Optional;

/**
 * AccountServiceImpl类实现了AccountService接口，负责处理账户相关的业务逻辑。
 * 这包括创建账户、存款、取款、转账以及获取账户信息等操作。
 * 该类使用了Spring的@Service注解，表明它是一个服务层组件，并且使用@Transactional注解确保业务操作的原子性。
 */
@Slf4j
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private BalanceService balanceService;

    private static final String ACCOUNT_CACHE_KEY_PREFIX = "account:";
    private static final long CACHE_TIMEOUT = 30; // 缓存30分钟

    /**
     * 系统初始化时加载所有账户信息到缓存。
     * 这个方法在服务启动后自动调用，用于预热缓存，提高系统响应速度。
     */
    @PostConstruct
    public void init() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + account.getId();
            cacheService.set(cacheKey, account, CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
    }

    /**
     * 创建新账户。
     *
     * @param request 包含创建账户所需信息的请求对象。
     * @return 返回创建成功的账户对象。
     * @throws RuntimeException 如果用户不存在或账户号码已存在，则抛出运行时异常。
     */
    @Override
    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("用户不存在");
        }

        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new RuntimeException("账户号码已存在");
        }

        Account account = accountMapper.toAccount(request);

        try {
            account = accountRepository.save(account);

            if (request.getBalance() != null && request.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
                transactionRequest.setTargetAccountId(account.getId());
                transactionRequest.setAmount(request.getBalance());
                transactionRequest.setType(TransactionType.DEPOSIT);
                transactionRequest.setDescription("开户存款");

                transactionService.createTransaction(transactionRequest);
            }

            // 保存到缓存
            String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + account.getId();
            cacheService.set(cacheKey, account, CACHE_TIMEOUT, TimeUnit.MINUTES);

            return account;
        } catch (Exception e) {
            log.error("创建账户失败", e);
            throw new RuntimeException("创建账户失败");
        }
    }

    /**
     * 更新账户余额。
     *
     * @param accountId 账户ID。
     * @param amount 要更新的金额。
     */
    @Override
    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount) {
        balanceService.updateBalance(accountId, amount);
    }

    /**
     * 根据账户ID获取账户信息。
     * 首先尝试从缓存中获取，如果缓存未命中，则从数据库中查询。
     *
     * @param id 账户ID。
     * @return 返回账户对象。
     * @throws RuntimeException 如果账户不存在，则抛出运行时异常。
     */
    @Override
    public Account getAccountById(Long id) {
        String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + id;
        
        // 先从缓存获取
        Account account = cacheService.get(cacheKey, Account.class);
        if (account != null) {
            log.info("Account found in cache: {}", cacheKey);
            return account;
        }
        
        // 缓存未命中，从数据库获取
        log.info("Account not found in cache, fetching from database: {}", id);
        account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found: " + id));
            
        // 放入缓存
        cacheService.set(cacheKey, account, CACHE_TIMEOUT, TimeUnit.MINUTES);
        return account;
    }

    /**
     * 获取所有账户信息。
     *
     * @return 返回账户对象列表。
     */
    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * 存款操作。
     *
     * @param accountId 账户ID。
     * @param amount 存款金额。
     * @return 返回更新后的账户对象。
     * @throws RuntimeException 如果存款金额不大于0或账户不存在，则抛出运行时异常。
     */
    @Override
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("存款金额必须大于0");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        try {
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setType(TransactionType.DEPOSIT);
            transactionRequest.setAmount(amount);
            transactionRequest.setTargetAccountId(accountId);
            transactionRequest.setDescription("存款");

            transactionService.createTransaction(transactionRequest);

            return accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("账户不存在"));
        } catch (Exception e) {
            log.error("存款失败", e);
            throw new RuntimeException("存款失败: " + e.getMessage());
        }
    }

    /**
     * 取款操作。
     *
     * @param accountId 账户ID。
     * @param amount 取款金额。
     * @return 返回更新后的账户对象。
     * @throws RuntimeException 如果取款金额不大于0、账户不存在或余额不足，则抛出运行时异常。
     */
    @Override
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("取款金额必须大于0");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        try {
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setType(TransactionType.WITHDRAW);
            transactionRequest.setAmount(amount);
            transactionRequest.setSourceAccountId(accountId);
            transactionRequest.setDescription("取款");

            transactionService.createTransaction(transactionRequest);

            return accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("账户不存在"));
        } catch (Exception e) {
            log.error("取款失败", e);
            throw new RuntimeException("取款失败: " + e.getMessage());
        }
    }

    /**
     * 转账操作。
     *
     * @param sourceAccountId 源账户ID。
     * @param targetAccountId 目标账户ID。
     * @param amount 转账金额。
     * @throws RuntimeException 如果转账金额不大于0、账户不存在或余额不足，则抛出运行时异常。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        log.info("开始转账流程 - 从账户: {} 到账户: {}, 金额: {}", sourceAccountId, targetAccountId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("转账金额必须大于0");
        }

        Account sourceAccount = accountRepository.findByIdWithLock(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("源账户不存在"));
        Account targetAccount = accountRepository.findByIdWithLock(targetAccountId)
                .orElseThrow(() -> new RuntimeException("目标账户不存在"));

        log.info("源账户当前余额: {}, 目标账户当前余额: {}",
                sourceAccount.getBalance(), targetAccount.getBalance());

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        try {
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setType(TransactionType.TRANSFER);
            transactionRequest.setAmount(amount);
            transactionRequest.setSourceAccountId(sourceAccountId);
            transactionRequest.setTargetAccountId(targetAccountId);
            transactionRequest.setDescription("转账交易");

            log.info("创建转账交易请求: {}", transactionRequest);

            Transaction transaction = transactionService.createTransaction(transactionRequest);
            log.info("转账交易记录创建成功，交易ID: {}", transaction.getId());

            Transaction savedTransaction = transactionService.getTransactionById(transaction.getId());
            log.info("已保存的交易记录: {}", savedTransaction);

        } catch (Exception e) {
            log.error("转账失败: {}", e.getMessage(), e);
            throw new RuntimeException("转账失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Account updateAccount(Account account) {
        Account updatedAccount = accountRepository.save(account);
        // 更新缓存
        String cacheKey = ACCOUNT_CACHE_KEY_PREFIX + updatedAccount.getId();
        cacheService.set(cacheKey, updatedAccount, CACHE_TIMEOUT, TimeUnit.MINUTES);
        return updatedAccount;
    }
}
