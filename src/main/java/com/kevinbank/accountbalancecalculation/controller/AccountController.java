package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import com.kevinbank.accountbalancecalculation.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * AccountController 类负责处理与账户相关的 HTTP 请求。
 * 它使用 AccountService 来执行具体的业务逻辑。
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    /**
     * 自动注入 AccountService 实例，用于处理账户相关的业务逻辑。
     */
    @Autowired
    private AccountService accountService;

    /**
     * 创建新账户。
     *
     * @param request 经过验证的请求体，包含创建账户所需的信息。
     * @return 返回包含新创建账户信息的响应实体。
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.ok(account);
    }

    /**
     * 更新账户余额。
     *
     * @param id 账户 ID。
     * @param amount 更新余额的金额。
     * @return 返回表示成功更新的响应实体。
     */
    @PutMapping("/{id}/balance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        accountService.updateBalance(id, amount);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取指定 ID 的账户信息。
     *
     * @param id 账户 ID。
     * @return 返回包含账户信息的响应实体。
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    /**
     * 获取所有账户信息。
     *
     * @return 返回包含所有账户信息的响应实体。
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
}
