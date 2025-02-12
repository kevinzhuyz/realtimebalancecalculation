package com.kevinbank.accountbalancecalculation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 交易记录模型类，用于表示银行系统中的每笔交易。
 * 包含交易的详细信息，如来源账户、目标账户、交易金额、类型、描述和交易时间。
 */
@Data
@Entity
@Table(name = "transactions")
@JsonSerialize
public class Transaction {
    /**
     * 交易的唯一标识符。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize
    private Long id;

    /**
     * 来源账户ID，标识资金从哪个账户转出。
     */
    @Column(name = "source_account_id")
    @JsonSerialize
    private Long sourceAccountId;

    /**
     * 目标账户ID，标识资金转入哪个账户。
     */
    @Column(name = "target_account_id")
    @JsonSerialize
    private Long targetAccountId;

    /**
     * 交易金额，不能为空，表示交易涉及的金额。
     */
    @Column(nullable = false)
    @JsonSerialize
    private BigDecimal amount;

    /**
     * 交易类型，不能为空，表示交易的类型（例如：存款、取款、转账）。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonSerialize
    private TransactionType type;

    /**
     * 交易描述，提供关于交易的额外信息。
     */
    @JsonSerialize
    private String description;

    /**
     * 交易时间，不能为空，记录交易发生的时间。
     */
    @Column(name = "transaction_time", nullable = false)
    @JsonSerialize
    private LocalDateTime transactionTime;

    /**
     * 在持久化交易之前设置交易时间。
     * 如果交易时间未设置，则默认为当前时间。
     */
    @PrePersist
    protected void onCreate() {
        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
    }
}
