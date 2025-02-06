package com.kevinbank.accountbalancecalculation.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private Long id;
    private String transactionId;
    private BigDecimal amount;
    private String sourceCardId;
    private String targetCardId;
    private LocalDateTime timestamp;
    private String tranType;
    private String sourceAccount;
    private String targetAccount;
} 