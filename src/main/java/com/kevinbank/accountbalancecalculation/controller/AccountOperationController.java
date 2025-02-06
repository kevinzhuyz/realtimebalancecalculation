package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts/operations")
public class AccountOperationController {
    
    private final AccountService accountService;
    
    public AccountOperationController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @PostMapping("/{cardId}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable String cardId,
            @RequestParam BigDecimal amount) {
        try {
            Account updatedAccount = accountService.deposit(cardId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Deposit failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/{cardId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String cardId,
            @RequestParam BigDecimal amount) {
        try {
            Account updatedAccount = accountService.withdraw(cardId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Withdrawal failed: " + e.getMessage());
        }
    }
} 