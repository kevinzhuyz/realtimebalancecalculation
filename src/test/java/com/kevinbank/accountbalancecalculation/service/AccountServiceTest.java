package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.model.TransactionType;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

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

    @BeforeEach
    void setUp() {
        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUserId(1L);
        mockAccount.setBalance(new BigDecimal("1000.00"));
        mockAccount.setCreditLimit(new BigDecimal("5000.00"));
        mockAccount.setAccountNumber("ACC001");
    }

    @Test
    void deposit_Success() {
        // 准备
        BigDecimal depositAmount = new BigDecimal("100.00");
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setBalance(new BigDecimal("1100.00"));

        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(mockAccount));
        when(accountRepository.save(any(Account.class)))
            .thenReturn(updatedAccount);

        // 执行
        Account result = accountService.deposit(1L, depositAmount);

        // 验证
        assertNotNull(result);
        assertEquals(new BigDecimal("1100.00"), result.getBalance());
        
        // 验证是否创建了存款交易记录
        verify(transactionService).createTransaction(argThat(request -> {
            assertEquals(TransactionType.DEPOSIT, request.getType());
            assertEquals(depositAmount, request.getAmount());
            assertEquals(1L, request.getTargetAccountId());
            return true;
        }));
    }

    @Test
    void deposit_AccountNotFound() {
        // 准备
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, 
            () -> accountService.deposit(1L, new BigDecimal("100.00")));
        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    void deposit_InvalidAmount() {
        // 准备
        BigDecimal invalidAmount = new BigDecimal("-100.00");

        // 执行和验证
        assertThrows(RuntimeException.class, 
            () -> accountService.deposit(1L, invalidAmount));
    }

    @Test
    void withdraw_Success() {
        // 准备
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setBalance(new BigDecimal("900.00"));

        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(mockAccount));
        when(accountRepository.save(any(Account.class)))
            .thenReturn(updatedAccount);

        // 执行
        Account result = accountService.withdraw(1L, withdrawAmount);

        // 验证
        assertNotNull(result);
        assertEquals(new BigDecimal("900.00"), result.getBalance());
        
        // 验证是否创建了取款交易记录
        verify(transactionService).createTransaction(argThat(request -> {
            assertEquals(TransactionType.WITHDRAW, request.getType());
            assertEquals(withdrawAmount, request.getAmount());
            assertEquals(1L, request.getSourceAccountId());
            return true;
        }));
    }

    @Test
    void withdraw_AccountNotFound() {
        // 准备
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行和验证
        assertThrows(RuntimeException.class, 
            () -> accountService.withdraw(1L, new BigDecimal("100.00")));
        verify(transactionService, never()).createTransaction(any());
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
    void withdraw_InvalidAmount() {
        // 准备
        BigDecimal invalidAmount = new BigDecimal("-100.00");

        // 执行和验证
        assertThrows(RuntimeException.class, 
            () -> accountService.withdraw(1L, invalidAmount));
    }

    @Test
    void transfer_Success() {
        // 准备
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setBalance(new BigDecimal("500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

        // 执行
        accountService.transfer(1L, 2L, transferAmount);

        // 验证是否创建了转账交易记录
        verify(transactionService).createTransaction(argThat(request -> {
            assertEquals(TransactionType.TRANSFER, request.getType());
            assertEquals(transferAmount, request.getAmount());
            assertEquals(1L, request.getSourceAccountId());
            assertEquals(2L, request.getTargetAccountId());
            return true;
        }));
    }
} 