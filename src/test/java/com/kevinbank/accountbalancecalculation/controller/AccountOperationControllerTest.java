package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountOperationControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountOperationController accountOperationController;

    private Account mockAccount;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUserId(1);
        mockAccount.setBalance(new BigDecimal("1000.00"));
        mockAccount.setCreditLimit(new BigDecimal("5000.00"));
        mockAccount.setAccountNumber("ACC001");
    }

    @Test
    void deposit_ShouldReturnUpdatedAccount() {
        // 准备
        BigDecimal depositAmount = new BigDecimal("100.00");
        when(accountService.deposit(eq(1L), any(BigDecimal.class)))
                .thenReturn(mockAccount);

        // 执行
        ResponseEntity<Account> response = accountOperationController.deposit(1L, depositAmount);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAccount, response.getBody());
    }

    @Test
    void withdraw_ShouldReturnUpdatedAccount() {
        // 准备
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        when(accountService.withdraw(eq(1L), any(BigDecimal.class)))
                .thenReturn(mockAccount);

        // 执行
        ResponseEntity<Account> response = accountOperationController.withdraw(1L, withdrawAmount);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAccount, response.getBody());
    }
} 