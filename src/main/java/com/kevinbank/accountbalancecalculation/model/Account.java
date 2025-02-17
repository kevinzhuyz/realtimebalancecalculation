package com.kevinbank.accountbalancecalculation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Account 类表示用户账户信息及其相关操作。
 * 包含用户的基本信息，如卡号、姓名、密码哈希值、性别、余额、信用额度和部门。
 * 使用 Lombok 的 @Getter 自动生成字段的 getter 方法，使用 @Setter 自动生成字段的 setter 方法。
 */
@Data
@Entity
@Table(name = "accounts")
@JsonSerialize
public class Account {
    /**
     * 主键，唯一标识一个账户记录。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize
    private Long id;

    /**
     * 用户ID，关联到用户信息。
     */
    @Column(name = "user_id", nullable = false)
    @JsonSerialize
    private Long userId;

    /**
     * 账户号码，每个账户的唯一标识。
     */
    @Column(unique = true, nullable = false)
    @JsonSerialize
    private String accountNumber;

    /**
     * 账户余额，默认为0。
     */
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    @JsonSerialize
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 信用额度，默认为0。
     */
    @Column(name = "credit_limit", nullable = false)
    @JsonSerialize
    private BigDecimal creditLimit = BigDecimal.ZERO;

    /**
     * 账户创建时间。
     */
    @Column(name = "created_at")
    @JsonSerialize
    private LocalDateTime createdAt;

    /**
     * 在持久化实体之前自动设置创建时间。
     * 这个方法由 JPA 调用，用于在实体被持久化到数据库之前设置创建时间。
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
