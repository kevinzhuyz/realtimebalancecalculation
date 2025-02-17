package com.kevinbank.accountbalancecalculation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 取款请求模型类
 */
@Data
public class WithdrawRequest {
    
    /**
     * 取款金额，必须大于0
     */
    @NotNull(message = "取款金额不能为空")
    @Positive(message = "取款金额必须大于0")
    private BigDecimal amount;
} 