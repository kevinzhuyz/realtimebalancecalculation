/**
 * 交易类，用于表示银行账户间的交易信息
 * 包含了交易的详细信息，如交易ID、金额、涉及的卡片和账户信息等
 */
package com.kevinbank.accountbalancecalculation.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易实体类
 * 使用@Data注解来自动生成getter和setter方法，简化代码和配置
 */
@Data
public class Transaction {
    // 交易的唯一标识符
    private Long id;
    // 交易ID，用于跟踪特定的交易
    private String transactionId;
    // 交易金额，使用BigDecimal以精确表示金融数据
    private BigDecimal amount;
    // 发起交易的卡片ID
    private String sourceCardId;
    // 接收交易的卡片ID
    private String targetCardId;
    // 交易发生的时间戳
    private LocalDateTime timestamp;
    // 交易类型，描述了交易的性质（如存款、取款等）
    private String tranType;
    // 发起交易的账户ID
    private String sourceAccount;
    // 接收交易的账户ID
    private String targetAccount;
}
