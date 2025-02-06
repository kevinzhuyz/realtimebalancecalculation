package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountServiceImpl(accountMapper, transactionMapper);
    }

    @Test
    void deposit_Success() {
        // Arrange
        Account account = new Account();
        account.setCardId(6L);
        account.setBalance(new BigDecimal("1000.00"));

        when(accountMapper.findById(6L)).thenReturn(account);
        when(accountMapper.updateBalance(any(), any())).thenReturn(1);
        when(transactionMapper.insert(any())).thenReturn(1);

        // Act
        Account result = accountService.deposit("6", new BigDecimal("100.00"));

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1100.00"), result.getBalance());
        verify(accountMapper, times(1)).updateBalance(eq(6L), eq(new BigDecimal("1100.00")));
    }

    @Test
    void withdraw_Success() {
        // Arrange
        Account account = new Account();
        account.setCardId(6L);
        account.setBalance(new BigDecimal("1000.00"));

        when(accountMapper.findById(6L)).thenReturn(account);
        when(accountMapper.updateBalance(any(), any())).thenReturn(1);
        when(transactionMapper.insert(any())).thenReturn(1);

        // Act
        Account result = accountService.withdraw("6", new BigDecimal("100.00"));

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("900.00"), result.getBalance());
        verify(accountMapper, times(1)).updateBalance(eq(6L), eq(new BigDecimal("900.00")));
    }

    @Test
    void withdraw_InsufficientBalance() {
        // Arrange
        Account account = new Account();
        account.setCardId(6L);
        account.setBalance(new BigDecimal("50.00"));

        when(accountMapper.findById(6L)).thenReturn(account);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            accountService.withdraw("6", new BigDecimal("100.00"))
        );
    }
} 