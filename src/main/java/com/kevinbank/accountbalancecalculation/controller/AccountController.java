package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.domain.Account;
import com.kevinbank.accountbalancecalculation.domain.Transaction;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    private final AccountService accountService;
    
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @GetMapping("/{cardId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long cardId) {
        Account account = accountService.getAccount(cardId);
        return account != null ? ResponseEntity.ok(account) : ResponseEntity.notFound().build();
    }
    
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
    
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(account));
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam String sourceCardId,
            @RequestParam String targetCardId,
            @RequestParam BigDecimal amount) {
        boolean success = accountService.transfer(sourceCardId, targetCardId, amount);
        return success ? 
                ResponseEntity.ok("Transfer successful") : 
                ResponseEntity.badRequest().body("Transfer failed");
    }
    
    @GetMapping("/{cardId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String cardId) {
        return ResponseEntity.ok(accountService.getTransactionHistory(cardId));
    }
} 