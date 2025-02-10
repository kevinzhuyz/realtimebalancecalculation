package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;
    private CreateAccountRequest createRequest;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);  // 使用 Long 类型
        testAccount.setUserId(1L);  // 使用 Long 类型
        testAccount.setAccountNumber("TEST001");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setCreditLimit(new BigDecimal("500.00"));

        createRequest = new CreateAccountRequest();
        createRequest.setUserId(1L);  // 使用 Long 类型
        createRequest.setAccountNumber("TEST001");
        createRequest.setBalance(new BigDecimal("1000.00"));
        createRequest.setCreditLimit(new BigDecimal("500.00"));
    }

    @Test
    void createAccount() throws Exception {
        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))  // 使用 Long 类型
                .andExpect(jsonPath("$.userId").value(1L))  // 使用 Long 类型
                .andExpect(jsonPath("$.accountNumber").value("TEST001"))
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }

    @Test
    void getAccount() throws Exception {
        when(accountService.getAccountById(1L))  // 使用 Long 类型
                .thenReturn(testAccount);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))  // 使用 Long 类型
                .andExpect(jsonPath("$.userId").value(1L))  // 使用 Long 类型
                .andExpect(jsonPath("$.accountNumber").value("TEST001"))
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }

    @Test
    void deposit() throws Exception {
        when(accountService.deposit(1L, new BigDecimal("100.00")))  // 使用 Long 类型
                .thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts/1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }

    @Test
    void withdraw() throws Exception {
        when(accountService.withdraw(1L, new BigDecimal("100.00")))  // 使用 Long 类型
                .thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts/1/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }
} 