package com.kevinbank.accountbalancecalculation.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 创建交易请求的模型类
 * 用于封装创建交易时所需的信息
 */
@Data
public class CreateTransactionRequest {
    /**
     * 源账户ID，用于标识交易发起的账户
     * 不能为空，否则在进行交易时无法确定是哪个账户发起的交易
     */
    @NotNull(message = "源账户ID不能为空")
    private Long sourceAccountId;  // 源账户ID

    /**
     * 目标账户ID，用于转账交易，标识接收转账的账户
     * 如果不是转账交易，该字段可以为空
     */
    private Long targetAccountId;  // 目标账户ID（转账时使用）

    /**
     * 交易金额，必须为正数，表示发生交易的金额大小
     * 不能为空，且必须大于0，否则交易没有意义
     */
    @NotNull(message = "交易金额不能为空")
    @Positive(message = "交易金额必须大于0")
    private BigDecimal amount;     // 交易金额

    /**
     * 交易类型，使用 TransactionType 枚举来定义
     * 不能为空，用于确定交易的类型（如存款、取款、转账等）
     */
    @NotNull(message = "交易类型不能为空")
    private TransactionType type;  // 使用 TransactionType 枚举

    /**
     * 交易描述，用于提供交易的额外信息
     * 可以为空，主要用于记录交易的相关说明或备注
     */
    private String description;    // 交易描述
}
