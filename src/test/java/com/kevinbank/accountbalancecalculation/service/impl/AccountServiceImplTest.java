package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account mockAccount;
    private CreateAccountRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUserId(1);
        mockAccount.setBalance(new BigDecimal("1000.00"));
        mockAccount.setCreditLimit(new BigDecimal("5000.00"));
        mockAccount.setAccountNumber("ACC001");

        mockRequest = new CreateAccountRequest();
        mockRequest.setUserId(1);
        mockRequest.setCreditLimit(new BigDecimal("5000.00"));
        mockRequest.setAccountNumber("ACC001");
    }

    @Test
    void createAccount_Success() {
        // 准备
        when(userRepository.existsById(1)).thenReturn(true);
        when(accountRepository.existsByAccountNumber("ACC001")).thenReturn(false);
        when(accountMapper.toAccount(any(CreateAccountRequest.class))).thenReturn(mockAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // 执行
        Account result = accountService.createAccount(mockRequest);

        // 验证
        assertNotNull(result);
        assertEquals(mockAccount.getId(), result.getId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_UserNotExists() {
        // 准备
        when(userRepository.existsById(1)).thenReturn(false);

        // 执行和验证
        assertThrows(RuntimeException.class, () -> accountService.createAccount(mockRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_AccountNumberExists() {
        // 准备
        when(userRepository.existsById(1)).thenReturn(true);
        when(accountRepository.existsByAccountNumber("ACC001")).thenReturn(true);

        // 执行和验证
        assertThrows(RuntimeException.class, () -> accountService.createAccount(mockRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void deposit_Success() {
        // 准备
        BigDecimal depositAmount = new BigDecimal("100.00");
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setBalance(new BigDecimal("1100.00"));

        // 设置 mock
        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(mockAccount))  // 第一次查询返回原始账户
            .thenReturn(Optional.of(updatedAccount));  // 第二次查询返回更新后的账户

        // 执行
        Account result = accountService.deposit(1L, depositAmount);

        // 验证
        assertNotNull(result);
        assertEquals(new BigDecimal("1100.00"), result.getBalance());
        verify(transactionService).createTransaction(argThat(request -> {
            assertEquals("DEPOSIT", request.getType());
            assertEquals(depositAmount, request.getAmount());
            assertEquals(1L, request.getTargetAccountId());
            return true;
        }));
    }

    @Test
    void withdraw_Success() {
        // 准备
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setBalance(new BigDecimal("900.00"));

        // 设置 mock
        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(mockAccount))  // 第一次查询返回原始账户
            .thenReturn(Optional.of(updatedAccount));  // 第二次查询返回更新后的账户

        // 执行
        Account result = accountService.withdraw(1L, withdrawAmount);

        // 验证
        assertNotNull(result);
        assertEquals(new BigDecimal("900.00"), result.getBalance());
        verify(transactionService).createTransaction(argThat(request -> {
            assertEquals("WITHDRAW", request.getType());
            assertEquals(withdrawAmount, request.getAmount());
            assertEquals(1L, request.getSourceAccountId());
            return true;
        }));
    }

    @Test
    void withdraw_InsufficientBalance() {
        // 准备
        BigDecimal withdrawAmount = new BigDecimal("2000.00");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        // 执行和验证
        assertThrows(RuntimeException.class, 
            () -> accountService.withdraw(1L, withdrawAmount));
        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    void getAllAccounts_Success() {
        // 准备
        List<Account> mockAccounts = Arrays.asList(mockAccount);
        when(accountRepository.findAll()).thenReturn(mockAccounts);

        // 执行
        List<Account> results = accountService.getAllAccounts();

        // 验证
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(mockAccount.getId(), results.get(0).getId());
    }

    @Test
    void transfer_Success() {
        // 准备
        BigDecimal transferAmount = new BigDecimal("100.00");
        
        // 执行
        accountService.transfer(1L, 2L, transferAmount);
        
        // 验证
        verify(transactionService).createTransaction(argThat(request -> {
            assertEquals("TRANSFER", request.getType());
            assertEquals(transferAmount, request.getAmount());
            assertEquals(1L, request.getSourceAccountId());
            assertEquals(2L, request.getTargetAccountId());
            return true;
        }));
    }
} 