package com.kevinbank.accountbalancecalculation.service;

import java.math.BigDecimal;

public interface BalanceService {
    void updateBalance(Long accountId, BigDecimal amount);
} 