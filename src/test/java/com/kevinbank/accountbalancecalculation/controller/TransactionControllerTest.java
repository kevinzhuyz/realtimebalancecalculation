package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction mockTransaction;
    private CreateTransactionRequest mockRequest;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setType("TRANSFER");

        mockRequest = new CreateTransactionRequest();
        mockRequest.setSourceAccountId(1L);
        mockRequest.setTargetAccountId(2L);
        mockRequest.setAmount(new BigDecimal("100.00"));
        mockRequest.setType("TRANSFER");
    }

    @Test
    void transfer_ShouldReturnTransaction() {
        // 准备
        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenReturn(mockTransaction);

        // 执行
        ResponseEntity<Transaction> response = transactionController.transfer(
                mockRequest.getSourceAccountId(),
                mockRequest.getTargetAccountId(),
                mockRequest.getAmount()
        );

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTransaction, response.getBody());
    }

    @Test
    void createTransaction_ShouldReturnTransaction() {
        // 准备
        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenReturn(mockTransaction);

        // 执行
        ResponseEntity<Transaction> response = transactionController.createTransaction(mockRequest);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTransaction, response.getBody());
    }

    @Test
    void getTransactionsByAccountId_ShouldReturnTransactionList() {
        // 准备
        List<Transaction> mockTransactions = Arrays.asList(mockTransaction);
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(mockTransactions);

        // 执行
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAccountId(1L);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTransactions, response.getBody());
    }

    @Test
    void getTransaction_ShouldReturnTransaction() {
        // 准备
        when(transactionService.getTransactionById(1L)).thenReturn(mockTransaction);

        // 执行
        ResponseEntity<Transaction> response = transactionController.getTransaction(1L);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTransaction, response.getBody());
    }

    @Test
    void getAllTransactions_ShouldReturnTransactionList() {
        // 准备
        List<Transaction> mockTransactions = Arrays.asList(mockTransaction);
        when(transactionService.getAllTransactions()).thenReturn(mockTransactions);

        // 执行
        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTransactions, response.getBody());
    }
} 