package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.model.TransactionType;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.repository.TransactionRepository;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TransactionServiceImplTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    private Account testSourceAccount;
    private Account testTargetAccount;

    @BeforeEach
    void setUp() {
        testSourceAccount = new Account();
        testSourceAccount.setUserId(1L);  // 使用 Long 类型
        testSourceAccount.setAccountNumber("TEST001");
        testSourceAccount.setBalance(new BigDecimal("1000.00"));
        testSourceAccount.setCreditLimit(new BigDecimal("500.00"));
        testSourceAccount = accountRepository.save(testSourceAccount);

        testTargetAccount = new Account();
        testTargetAccount.setUserId(2L);  // 使用 Long 类型
        testTargetAccount.setAccountNumber("TEST002");
        testTargetAccount.setBalance(new BigDecimal("500.00"));
        testTargetAccount.setCreditLimit(new BigDecimal("500.00"));
        testTargetAccount = accountRepository.save(testTargetAccount);
    }

    @Test
    void testTransfer() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSourceAccountId(testSourceAccount.getId());
        request.setTargetAccountId(testTargetAccount.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setType(TransactionType.TRANSFER);
        request.setDescription("转账测试");
        
        Transaction transaction = transactionService.createTransaction(request);
        
        assertNotNull(transaction);
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(testSourceAccount.getId(), transaction.getSourceAccountId());
        assertEquals(testTargetAccount.getId(), transaction.getTargetAccountId());
        
        // 验证账户余额变化
        Account updatedSourceAccount = accountRepository.findById(testSourceAccount.getId()).get();
        Account updatedTargetAccount = accountRepository.findById(testTargetAccount.getId()).get();
        assertEquals(new BigDecimal("900.00"), updatedSourceAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), updatedTargetAccount.getBalance());
    }

    @Test
    void testDeposit() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTargetAccountId(testSourceAccount.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setType(TransactionType.DEPOSIT);
        request.setDescription("存款测试");
        
        Transaction transaction = transactionService.createTransaction(request);
        
        assertNotNull(transaction);
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        
        // 验证账户余额增加
        Account updatedAccount = accountRepository.findById(testSourceAccount.getId()).get();
        assertEquals(new BigDecimal("1100.00"), updatedAccount.getBalance());
    }

    @Test
    void testWithdraw() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSourceAccountId(testSourceAccount.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setType(TransactionType.WITHDRAW);
        request.setDescription("取款测试");
        
        Transaction transaction = transactionService.createTransaction(request);
        
        assertNotNull(transaction);
        assertEquals(TransactionType.WITHDRAW, transaction.getType());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        
        // 验证账户余额减少
        Account updatedAccount = accountRepository.findById(testSourceAccount.getId()).get();
        assertEquals(new BigDecimal("900.00"), updatedAccount.getBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSourceAccountId(testSourceAccount.getId());
        request.setAmount(new BigDecimal("2000.00"));  // 超过余额和信用额度
        request.setType(TransactionType.WITHDRAW);
        request.setDescription("取款测试-余额不足");
        
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(request);
        });
        
        // 验证账户余额未变
        Account updatedAccount = accountRepository.findById(testSourceAccount.getId()).get();
        assertEquals(new BigDecimal("1000.00"), updatedAccount.getBalance());
    }
} 