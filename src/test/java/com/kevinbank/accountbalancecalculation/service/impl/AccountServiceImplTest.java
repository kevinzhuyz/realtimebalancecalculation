package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@Transactional
class AccountServiceImplTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;
    private CreateAccountRequest createRequest;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setUserId(1L);  // 使用 Long 类型
        testAccount.setAccountNumber("TEST001");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setCreditLimit(new BigDecimal("500.00"));
        testAccount = accountRepository.save(testAccount);

        createRequest = new CreateAccountRequest();
        createRequest.setUserId(1L);  // 使用 Long 类型
        createRequest.setAccountNumber("TEST002");
        createRequest.setBalance(new BigDecimal("1000.00"));
        createRequest.setCreditLimit(new BigDecimal("500.00"));
    }

    @Test
    void createAccount() {
        Account account = accountService.createAccount(createRequest);
        assertNotNull(account);
        assertEquals(createRequest.getUserId(), account.getUserId());
        assertEquals(createRequest.getAccountNumber(), account.getAccountNumber());
        assertEquals(createRequest.getBalance(), account.getBalance());
    }

    @Test
    void deposit() {
        Account account = accountService.deposit(testAccount.getId(), new BigDecimal("100.00"));
        assertEquals(new BigDecimal("1100.00"), account.getBalance());
    }

    @Test
    void withdraw() {
        Account account = accountService.withdraw(testAccount.getId(), new BigDecimal("100.00"));
        assertEquals(new BigDecimal("900.00"), account.getBalance());
    }

    @Test
    void withdrawInsufficientFunds() {
        assertThrows(RuntimeException.class, () -> {
            accountService.withdraw(testAccount.getId(), new BigDecimal("2000.00"));
        });
    }

    @Test
    void transfer() {
        // 创建目标账户
        Account targetAccount = new Account();
        targetAccount.setUserId(2L);  // 使用 Long 类型
        targetAccount.setAccountNumber("TEST003");
        targetAccount.setBalance(new BigDecimal("500.00"));
        targetAccount.setCreditLimit(new BigDecimal("500.00"));
        targetAccount = accountRepository.save(targetAccount);

        // 执行转账
        accountService.transfer(testAccount.getId(), targetAccount.getId(), new BigDecimal("100.00"));

        // 验证余额变化
        Account updatedSource = accountService.getAccountById(testAccount.getId());
        Account updatedTarget = accountService.getAccountById(targetAccount.getId());
        assertEquals(new BigDecimal("900.00"), updatedSource.getBalance());
        assertEquals(new BigDecimal("600.00"), updatedTarget.getBalance());
    }
} 