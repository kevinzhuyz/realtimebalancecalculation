package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.DepositRequest;
import com.kevinbank.accountbalancecalculation.model.WithdrawRequest;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 控制账户操作的控制器，处理与账户相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountOperationController {

    /**
     * 注入账户服务，用于执行账户相关的业务逻辑
     */
    @Autowired
    private AccountService accountService;

    /**
     * 存款操作
     *
     * @param accountId 账户ID
     * @param request 存款请求体，包含存款金额
     * @return 更新后的账户信息
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody DepositRequest request) {
        log.info("存款请求 - 账户ID: {}, 金额: {}", accountId, request.getAmount());
        try {
            Account updatedAccount = accountService.deposit(accountId, request.getAmount());
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("存款失败", e);
            throw e;
        }
    }

    /**
     * 处理取款请求
     *
     * @param accountId 账户ID
     * @param request 取款请求体，包含取款金额
     * @return 更新后的账户信息
     */
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody WithdrawRequest request) {
        log.info("取款请求 - 账户ID: {}, 金额: {}", accountId, request.getAmount());
        try {
            Account account = accountService.withdraw(accountId, request.getAmount());
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            log.error("取款失败", e);
            throw e;
        }
    }
}
