package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.service.AccountService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@WebMvcTest(AccountOperationController.class)
class AccountOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUserId(1L);
        testAccount.setAccountNumber("TEST001");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setCreditLimit(new BigDecimal("500.00"));
    }

    @Test
    void deposit() throws Exception {
        when(accountService.deposit(eq(1L), any(BigDecimal.class)))
                .thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts/1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }

    @Test
    void withdraw() throws Exception {
        when(accountService.withdraw(eq(1L), any(BigDecimal.class)))
                .thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts/1/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }

    @Test
    void transfer() throws Exception {
        mockMvc.perform(post("/api/accounts/1/transfer/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100.00"))
                .andExpect(status().isOk());
    }
} 