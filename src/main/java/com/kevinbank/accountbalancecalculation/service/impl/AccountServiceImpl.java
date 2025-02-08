package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountMapper accountMapper;
    
    @Autowired
    private TransactionService transactionService;
    
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
            return accountRepository.save(account);
        } catch (Exception e) {
            log.error("创建账户失败", e);
            throw new RuntimeException("创建账户失败");
        }
    }
    
    @Override
    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
                
        BigDecimal newBalance = account.getBalance().add(amount);
        
        // 如果是扣款，检查余额是否足够
        if (amount.compareTo(BigDecimal.ZERO) < 0 
                && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
    }
    
    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    @Override
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        // 验证参数
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("存款金额必须大于0");
        }

        // 先验证账户是否存在
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        // 创建存款交易记录
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setTargetAccountId(accountId);
        transactionRequest.setAmount(amount);
        transactionRequest.setType("DEPOSIT");
        transactionService.createTransaction(transactionRequest);

        // 返回更新后的账户
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
    }
    
    @Override
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        // 验证参数
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("取款金额必须大于0");
        }

        // 先验证账户是否存在
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        // 验证余额是否足够
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 创建取款交易记录
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setSourceAccountId(accountId);
        transactionRequest.setAmount(amount);
        transactionRequest.setType("WITHDRAW");
        transactionService.createTransaction(transactionRequest);

        // 返回更新后的账户
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
    }
    
    @Override
    @Transactional
    public void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        // 验证参数
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("转账金额必须大于0");
        }
        
        if (sourceAccountId.equals(targetAccountId)) {
            throw new RuntimeException("不能转账给自己");
        }

        // 创建转账交易记录
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setSourceAccountId(sourceAccountId);
        transactionRequest.setTargetAccountId(targetAccountId);
        transactionRequest.setAmount(amount);
        transactionRequest.setType("TRANSFER");
        transactionService.createTransaction(transactionRequest);
    }
} 