package com.kevinbank.accountbalancecalculation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "账户号码不能为空")
    private String accountNumber;
    
    @NotNull(message = "信用额度不能为空")
    @Positive(message = "信用额度必须大于0")
    private BigDecimal creditLimit;
    
    private BigDecimal balance;
} 