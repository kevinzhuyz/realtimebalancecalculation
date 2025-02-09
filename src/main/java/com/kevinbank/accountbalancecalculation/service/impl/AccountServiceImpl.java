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
    
    private static final String ACCOUNT_CACHE_KEY = "account:";
    private static final long CACHE_DURATION = 5; // 缓存时间（分钟）
    
    @PostConstruct
    public void init() {
        // 系统启动时预热缓存
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            String cacheKey = ACCOUNT_CACHE_KEY + account.getId();
            cacheService.set(cacheKey, account, CACHE_DURATION, TimeUnit.MINUTES);
        }
    }
    
    @Override
    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        // 验证用户是否存在
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证账户号码是否已存在
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new RuntimeException("账户号码已存在");
        }
        
        // 使用 MapStruct 转换对象
        Account account = accountMapper.toAccount(request);
        
        try {
            // 保存账户
            account = accountRepository.save(account);
            
            // 如果有初始余额，创建存款交易
            if (request.getBalance() != null && request.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
                transactionRequest.setTargetAccountId(account.getId());
                transactionRequest.setAmount(request.getBalance());
                transactionRequest.setType(TransactionType.DEPOSIT);  // 使用枚举
                transactionRequest.setDescription("开户存款");
                
                transactionService.createTransaction(transactionRequest);
            }
            
            return account;
        } catch (Exception e) {
            log.error("创建账户失败", e);
            throw new RuntimeException("创建账户失败");
        }
    }
    
    @Override
    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount) {
        balanceService.updateBalance(accountId, amount);
    }
    
    @Override
    public Account getAccountById(Long accountId) {
        String cacheKey = ACCOUNT_CACHE_KEY + accountId;
        
        // 先从缓存获取
        Account account = cacheService.get(cacheKey, Account.class);
        if (account != null) {
            return account;
        }
        
        // 缓存未命中，从数据库查询
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (!optionalAccount.isPresent()) {
            // 对空值也进行缓存，防止缓存穿透
            cacheService.set(cacheKey, new Account(), 5, TimeUnit.MINUTES);
            throw new RuntimeException("账户不存在");
        }
        
        account = optionalAccount.get();
        cacheService.set(cacheKey, account, CACHE_DURATION, TimeUnit.MINUTES);
        
        return account;
    }
    
    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    @Override
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("存款金额必须大于0");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        try {
            // 创建存款交易记录
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setType(TransactionType.DEPOSIT);
            transactionRequest.setAmount(amount);
            transactionRequest.setTargetAccountId(accountId);
            transactionRequest.setDescription("存款");
            
            // 创建交易记录，余额更新由 TransactionService 处理
            transactionService.createTransaction(transactionRequest);
            
            // 重新获取更新后的账户信息
            return accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("账户不存在"));
        } catch (Exception e) {
            log.error("存款失败", e);
            throw new RuntimeException("存款失败: " + e.getMessage());
        }
    }
    
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
            // 创建取款交易记录
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setType(TransactionType.WITHDRAW);
            transactionRequest.setAmount(amount);
            transactionRequest.setSourceAccountId(accountId);
            transactionRequest.setDescription("取款");
            
            // 创建交易记录，余额更新由 TransactionService 处理
            transactionService.createTransaction(transactionRequest);
            
            // 重新获取更新后的账户信息
            return accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("账户不存在"));
        } catch (Exception e) {
            log.error("取款失败", e);
            throw new RuntimeException("取款失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)  // 明确指定回滚异常
    public void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        log.info("开始转账流程 - 从账户: {} 到账户: {}, 金额: {}", sourceAccountId, targetAccountId, amount);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("转账金额必须大于0");
        }

        // 检查账户是否存在
        Account sourceAccount = accountRepository.findByIdWithLock(sourceAccountId)  // 使用悲观锁
                .orElseThrow(() -> new RuntimeException("源账户不存在"));
        Account targetAccount = accountRepository.findByIdWithLock(targetAccountId)  // 使用悲观锁
                .orElseThrow(() -> new RuntimeException("目标账户不存在"));

        log.info("源账户当前余额: {}, 目标账户当前余额: {}", 
                sourceAccount.getBalance(), targetAccount.getBalance());

        // 检查余额是否充足
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        try {
            // 创建转账交易记录
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setType(TransactionType.TRANSFER);
            transactionRequest.setAmount(amount);
            transactionRequest.setSourceAccountId(sourceAccountId);
            transactionRequest.setTargetAccountId(targetAccountId);
            transactionRequest.setDescription("转账交易");
            
            log.info("创建转账交易请求: {}", transactionRequest);
            
            // 创建交易记录
            Transaction transaction = transactionService.createTransaction(transactionRequest);
            log.info("转账交易记录创建成功，交易ID: {}", transaction.getId());
            
            // 验证交易记录是否正确保存
            Transaction savedTransaction = transactionService.getTransactionById(transaction.getId());
            log.info("已保存的交易记录: {}", savedTransaction);
            
        } catch (Exception e) {
            log.error("转账失败: {}", e.getMessage(), e);
            throw new RuntimeException("转账失败: " + e.getMessage());
        }
    }
} 