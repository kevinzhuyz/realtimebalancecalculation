package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.service.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 实现余额服务的类
 */
@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {

    /**
     * 注入账户仓库，用于访问数据库中的账户信息
     */
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 更新账户余额的方法
     *
     * @param accountId 账户ID
     * @param amount 要更新的金额
     * @throws RuntimeException 如果账户不存在或余额不足时抛出异常
     */
    @Override
    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount) {
        // 获取账户信息，如果不存在则抛出异常
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        // 计算新余额
        BigDecimal newBalance = account.getBalance().add(amount);

        // 检查是否超出信用额度
        if (newBalance.compareTo(account.getCreditLimit().negate()) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 更新余额
        account.setBalance(newBalance);
        // 保存更新后的账户信息
        accountRepository.save(account);
    }
}
