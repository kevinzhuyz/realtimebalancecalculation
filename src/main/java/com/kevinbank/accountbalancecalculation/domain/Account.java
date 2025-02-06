package com.kevinbank.accountbalancecalculation.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Account {
    private Long cardId;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private String accountNumber;
} 