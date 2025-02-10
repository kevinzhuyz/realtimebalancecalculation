package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.model.TransactionType;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction testTransaction;
    private CreateTransactionRequest createRequest;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setSourceAccountId(1L);
        testTransaction.setTargetAccountId(2L);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setType(TransactionType.TRANSFER);  // 使用枚举
        testTransaction.setDescription("测试交易");
        testTransaction.setTransactionTime(LocalDateTime.now());

        createRequest = new CreateTransactionRequest();
        createRequest.setSourceAccountId(1L);
        createRequest.setTargetAccountId(2L);
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setType(TransactionType.TRANSFER);  // 使用枚举
        createRequest.setDescription("测试交易");
    }

    @Test
    void createTransaction() throws Exception {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sourceAccountId").value(1L))
                .andExpect(jsonPath("$.targetAccountId").value(2L))
                .andExpect(jsonPath("$.amount").value("100.00"))
                .andExpect(jsonPath("$.type").value(TransactionType.TRANSFER.name()));  // 使用枚举名称
    }

    @Test
    void getTransactionsByAccount() throws Exception {
        when(transactionService.getTransactionsByAccountId(1L))
                .thenReturn(Arrays.asList(testTransaction));

        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value(TransactionType.TRANSFER.name()));  // 使用枚举名称
    }

    @Test
    void getTransaction() throws Exception {
        when(transactionService.getTransactionById(1L))
                .thenReturn(testTransaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value(TransactionType.TRANSFER.name()));  // 使用枚举名称
    }
} 