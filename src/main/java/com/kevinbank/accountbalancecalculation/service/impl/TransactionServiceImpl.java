package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.repository.TransactionRepository;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionMapper transactionMapper;
    
    @Override
    @Transactional
    public Transaction createTransaction(CreateTransactionRequest request) {
        // 验证交易类型
        switch (request.getType()) {
            case "DEPOSIT":
                validateDepositRequest(request);
                executeDeposit(request);
                break;
            case "WITHDRAW":
                validateWithdrawRequest(request);
                executeWithdraw(request);
                break;
            case "TRANSFER":
                validateTransferRequest(request);
                executeTransfer(request);
                break;
            default:
                throw new RuntimeException("不支持的交易类型");
        }

        // 创建交易记录
        Transaction transaction = transactionMapper.toTransaction(request);
        return transactionRepository.save(transaction);
    }
    
    private void validateDepositRequest(CreateTransactionRequest request) {
        if (request.getTargetAccountId() == null) {
            throw new RuntimeException("存款账户ID不能为空");
        }
        validateAccount(request.getTargetAccountId());
    }
    
    private void validateWithdrawRequest(CreateTransactionRequest request) {
        if (request.getSourceAccountId() == null) {
            throw new RuntimeException("取款账户ID不能为空");
        }
        validateAccount(request.getSourceAccountId());
    }
    
    private void validateTransferRequest(CreateTransactionRequest request) {
        if (request.getSourceAccountId() == null || request.getTargetAccountId() == null) {
            throw new RuntimeException("转账的源账户和目标账户ID不能为空");
        }
        validateAccount(request.getSourceAccountId());
        validateAccount(request.getTargetAccountId());
    }
    
    private void validateAccount(Long accountId) {
        // 只验证账户存在性，不加锁
        if (!accountRepository.findById(accountId).isPresent()) {
            throw new RuntimeException("账户不存在: " + accountId);
        }
    }
    
    private void executeDeposit(CreateTransactionRequest request) {
        Account account = accountRepository.findByIdWithLock(request.getTargetAccountId())
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
    }
    
    private void executeWithdraw(CreateTransactionRequest request) {
        Account account = accountRepository.findByIdWithLock(request.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("余额不足");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
    }
    
    private void executeTransfer(CreateTransactionRequest request) {
        Account sourceAccount = accountRepository.findByIdWithLock(request.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("源账户不存在"));
        Account targetAccount = accountRepository.findByIdWithLock(request.getTargetAccountId())
                .orElseThrow(() -> new RuntimeException("目标账户不存在"));

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 执行转账
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));
        
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }
    
    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findBySourceAccountIdOrTargetAccountId(accountId, accountId);
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