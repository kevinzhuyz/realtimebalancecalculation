package com.kevinbank.accountbalancecalculation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    
    private Long sourceAccountId;
    
    private Long targetAccountId;
    
    @NotNull(message = "交易金额不能为空")
    @Positive(message = "交易金额必须大于0")
    private BigDecimal amount;
    
    @NotNull(message = "交易类型不能为空")
    private String type;
} 