package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.service.AccountService;
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
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account mockAccount;
    private CreateAccountRequest mockRequest;

    @BeforeEach
    void setUp() {
        // 准备测试数据
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
    void createAccount_ShouldReturnAccount() {
        // 准备
        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(mockAccount);

        // 执行
        ResponseEntity<Account> response = accountController.createAccount(mockRequest);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAccount, response.getBody());
    }

    @Test
    void updateBalance_ShouldReturnNoContent() {
        // 执行
        ResponseEntity<Void> response = accountController.updateBalance(1L, new BigDecimal("100.00"));

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getAccount_ShouldReturnAccount() {
        // 准备
        when(accountService.getAccountById(1L)).thenReturn(mockAccount);

        // 执行
        ResponseEntity<Account> response = accountController.getAccount(1L);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAccount, response.getBody());
    }

    @Test
    void getAllAccounts_ShouldReturnAccountList() {
        // 准备
        List<Account> mockAccounts = Arrays.asList(mockAccount);
        when(accountService.getAllAccounts()).thenReturn(mockAccounts);

        // 执行
        ResponseEntity<List<Account>> response = accountController.getAllAccounts();

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAccounts, response.getBody());
    }
} 