package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.domain.Transaction;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);
    }

    @Test
    void transfer_Success() {
        // Arrange
        Transaction mockTransaction = new Transaction();
        mockTransaction.setTransactionId("123");
        when(transactionService.transfer(any(), any(), any())).thenReturn(mockTransaction);

        // Act
        ResponseEntity<?> response = transactionController.transfer(
            "6", "7", new BigDecimal("100.00")
        );

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(mockTransaction, response.getBody());
    }

    @Test
    void transfer_InvalidAmount() {
        // Act
        ResponseEntity<?> response = transactionController.transfer(
            "6", "7", new BigDecimal("-100.00")
        );

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(transactionService, never()).transfer(any(), any(), any());
    }

    @Test
    void getTransactionHistory_Success() {
        // Arrange
        List<Transaction> mockTransactions = Arrays.asList(
            new Transaction(), new Transaction()
        );
        when(transactionService.getTransactionsByCardId("6")).thenReturn(mockTransactions);

        // Act
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionHistory("6");

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(2, response.getBody().size());
    }
} 