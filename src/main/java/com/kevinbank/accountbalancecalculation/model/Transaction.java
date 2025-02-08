package com.kevinbank.accountbalancecalculation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;  // DEPOSIT, WITHDRAW, TRANSFER
    
    @Column(name = "source_account_id")
    private Long sourceAccountId;
    
    @Column(name = "target_account_id")
    private Long targetAccountId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 