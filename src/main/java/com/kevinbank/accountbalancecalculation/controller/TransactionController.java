package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import com.kevinbank.accountbalancecalculation.model.TransactionType;
import com.kevinbank.accountbalancecalculation.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * TransactionController 类负责处理与交易相关的 HTTP 请求。
 * 它使用 TransactionService 来处理具体的业务逻辑。
 */
@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 执行转账操作。
     *
     * @param sourceAccountId 发起转账的账户 ID。
     * @param targetAccountId 接收转账的账户 ID。
     * @param amount 转账金额。
     * @return 创建的交易记录。
     * @throws Exception 如果转账过程中发生错误。
     */
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
        request.setType(TransactionType.TRANSFER);
        request.setDescription("转账交易");

        try {
            Transaction transaction = transactionService.createTransaction(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("转账失败", e);
            throw e;
        }
    }

    /**
     * 创建一个新的交易记录。
     *
     * @param request 创建交易的请求对象，包含交易的详细信息。
     * @return 创建的交易记录。
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        log.info("Creating transaction: {}", request);
        Transaction transaction = transactionService.createTransaction(request);
        return ResponseEntity.ok(transaction);
    }

    /**
     * 根据账户 ID 获取交易记录列表。
     *
     * @param accountId 账户 ID。
     * @return 账户对应的交易记录列表。
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * 根据交易 ID 获取交易记录。
     *
     * @param id 交易 ID。
     * @return 交易记录。
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * 获取所有交易记录。
     *
     * @return 所有交易记录列表。
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
