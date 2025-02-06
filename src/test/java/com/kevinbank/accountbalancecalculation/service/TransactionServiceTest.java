package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.domain.Transaction;
import com.kevinbank.accountbalancecalculation.mapper.AccountMapper;
import com.kevinbank.accountbalancecalculation.mapper.TransactionMapper;
import com.kevinbank.accountbalancecalculation.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionServiceImpl(accountMapper, transactionMapper);
    }

    @Test
    void transfer_Success() {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setCardId("6");
        sourceAccount.setBalance(new BigDecimal("1000.00"));

        Account targetAccount = new Account();
        targetAccount.setCardId("7");
        targetAccount.setBalance(new BigDecimal("500.00"));

        when(accountMapper.findById(6L)).thenReturn(sourceAccount);
        when(accountMapper.findById(7L)).thenReturn(targetAccount);
        when(accountMapper.updateBalance(any(), any())).thenReturn(1);
        when(transactionMapper.insert(any())).thenReturn(1);

        // Act
        Transaction result = transactionService.transfer("6", "7", new BigDecimal("100.00"));

        // Assert
        assertNotNull(result);
        assertEquals("TRANSFER", result.getTranType());
        assertEquals("6", result.getSourceCardId());
        assertEquals("7", result.getTargetCardId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(accountMapper, times(1)).updateBalance(eq("6"), eq(new BigDecimal("900.00")));
        verify(accountMapper, times(1)).updateBalance(eq("7"), eq(new BigDecimal("600.00")));
    }

    @Test
    void transfer_InsufficientBalance() {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setCardId("6");
        sourceAccount.setBalance(new BigDecimal("50.00"));

        Account targetAccount = new Account();
        targetAccount.setCardId("7");
        targetAccount.setBalance(new BigDecimal("500.00"));

        when(accountMapper.findById(6L)).thenReturn(sourceAccount);
        when(accountMapper.findById(7L)).thenReturn(targetAccount);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.transfer("6", "7", new BigDecimal("100.00"))
        );
    }

    @Test
    void getTransactionsByCardId_Success() {
        // Arrange
        String cardId = "6";
        List<Transaction> expectedTransactions = Arrays.asList(
            new Transaction(), new Transaction()
        );
        when(transactionMapper.findByCardId(cardId)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByCardId(cardId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionMapper, times(1)).findByCardId(cardId);
    }
} 