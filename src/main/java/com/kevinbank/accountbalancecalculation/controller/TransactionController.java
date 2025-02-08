package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(
            @RequestParam Long sourceAccountId,
            @RequestParam Long targetAccountId,
            @RequestParam BigDecimal amount) {
        log.info("转账请求 - 从账户: {}, 到账户: {}, 金额: {}", 
                sourceAccountId, targetAccountId, amount);
                
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSourceAccountId(sourceAccountId);
        request.setTargetAccountId(targetAccountId);
        request.setAmount(amount);
        request.setType("TRANSFER");
        
        try {
            Transaction transaction = transactionService.createTransaction(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("转账失败", e);
            throw e;
        }
    }
    
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        log.info("创建交易请求 - 类型: {}, 金额: {}", request.getType(), request.getAmount());
        try {
            Transaction transaction = transactionService.createTransaction(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("创建交易失败", e);
            throw e;
        }
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(
            @PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
} 