package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountOperationController {
    
    @Autowired
    private AccountService accountService;
    
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        log.info("存款请求 - 账户ID: {}, 金额: {}", id, amount);
        try {
            Account account = accountService.deposit(id, amount);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            log.error("存款失败", e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        log.info("取款请求 - 账户ID: {}, 金额: {}", id, amount);
        try {
            Account account = accountService.withdraw(id, amount);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            log.error("取款失败", e);
            throw e;
        }
    }
} 