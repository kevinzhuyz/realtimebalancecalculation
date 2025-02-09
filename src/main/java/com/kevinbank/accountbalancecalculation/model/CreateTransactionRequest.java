package com.kevinbank.accountbalancecalculation.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @NotNull(message = "源账户ID不能为空")
    private Long sourceAccountId;  // 源账户ID
    
    private Long targetAccountId;  // 目标账户ID（转账时使用）
    
    @NotNull(message = "交易金额不能为空")
    @Positive(message = "交易金额必须大于0")
    private BigDecimal amount;     // 交易金额
    
    @NotNull(message = "交易类型不能为空")
    private TransactionType type;  // 使用 TransactionType 枚举
    
    private String description;    // 交易描述
} 