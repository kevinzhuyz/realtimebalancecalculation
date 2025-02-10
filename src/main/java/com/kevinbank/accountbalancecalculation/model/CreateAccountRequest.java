package com.kevinbank.accountbalancecalculation.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建账户请求模型类
 * 用于处理创建账户时需要的信息和参数
 */
@Data
public class CreateAccountRequest {
    /**
     * 用户ID，用于关联账户到特定的用户
     * 不能为空，因为每个账户必须关联到一个用户
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 账户号码，用于唯一标识一个账户
     * 不能为空，因为每个账户必须有一个账户号码
     */
    @NotNull(message = "账户号码不能为空")
    private String accountNumber;

    /**
     * 信用额度，指账户的最大透支限额
     * 不能为空，并且必须大于0，以确保账户有有效的信用额度
     */
    @NotNull(message = "信用额度不能为空")
    @Positive(message = "信用额度必须大于0")
    private BigDecimal creditLimit;

    /**
     * 账户余额，表示账户当前的可用金额
     * 可以为空，因为在创建账户时可能不需要立即设定余额
     */
    private BigDecimal balance;
}
