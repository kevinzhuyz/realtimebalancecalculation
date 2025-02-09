package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.repository.AccountRepository;
import com.kevinbank.accountbalancecalculation.service.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount) {
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
        accountRepository.save(account);
    }
} 