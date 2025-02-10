package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.service.AccountService;
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
     * 处理存款请求
     *
     * @param id 账户ID
     * @param amount 存款金额
     * @return 更新后的账户信息
     */
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        // 记录存款请求的日志
        log.info("存款请求 - 账户ID: {}, 金额: {}", id, amount);
        try {
            // 调用服务层方法执行存款操作
            Account account = accountService.deposit(id, amount);
            // 返回更新后的账户信息
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            // 记录存款失败的错误日志
            log.error("存款失败", e);
            throw e;
        }
    }

    /**
     * 处理取款请求
     *
     * @param id 账户ID
     * @param amount 取款金额
     * @return 更新后的账户信息
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        // 记录取款请求的日志
        log.info("取款请求 - 账户ID: {}, 金额: {}", id, amount);
        try {
            // 调用服务层方法执行取款操作
            Account account = accountService.withdraw(id, amount);
            // 返回更新后的账户信息
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            // 记录取款失败的错误日志
            log.error("取款失败", e);
            throw e;
        }
    }
}
