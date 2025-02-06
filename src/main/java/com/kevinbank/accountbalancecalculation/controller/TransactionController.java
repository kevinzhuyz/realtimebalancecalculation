package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.domain.Transaction;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestParam String sourceCardId,
            @RequestParam String targetCardId,
            @RequestParam BigDecimal amount) {
        logger.info("Transfer request received - From: {}, To: {}, Amount: {}", 
                sourceCardId, targetCardId, amount);
                
        // 金额校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            String errorMsg = "Transfer amount must be positive";
            logger.error(errorMsg + " - Amount: {}", amount);
            return ResponseEntity.badRequest().body(errorMsg);
        }
        
        try {
            Transaction transaction = transactionService.transfer(sourceCardId, targetCardId, amount);
            logger.info("Transfer successful - Transaction ID: {}", transaction.getTransactionId());
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            logger.error("Transfer failed with illegal argument - {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Transfer failed with exception", e);
            return ResponseEntity.internalServerError().body("Transfer failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/account/{cardId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String cardId) {
        logger.info("Fetching transaction history for card ID: {}", cardId);
        try {
            List<Transaction> transactions = transactionService.getTransactionsByCardId(cardId);
            logger.info("Found {} transactions for card ID: {}", transactions.size(), cardId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error fetching transaction history for card ID: {}", cardId, e);
            throw e;
        }
    }
    
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String transactionId) {
        logger.info("Fetching transaction details for ID: {}", transactionId);
        Transaction transaction = transactionService.getTransactionById(transactionId);
        if (transaction != null) {
            logger.info("Transaction found: {}", transaction);
            return ResponseEntity.ok(transaction);
        } else {
            logger.warn("Transaction not found for ID: {}", transactionId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        logger.info("Fetching all transactions");
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            logger.info("Found {} transactions in total", transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error fetching all transactions", e);
            throw e;
        }
    }
} 