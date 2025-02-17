package com.kevinbank.accountbalancecalculation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 存款请求模型类
 */
@Data
public class DepositRequest {
    
    /**
     * 存款金额，必须大于0
     */
    @NotNull(message = "存款金额不能为空")
    @Positive(message = "存款金额必须大于0")
    private BigDecimal amount;
} 