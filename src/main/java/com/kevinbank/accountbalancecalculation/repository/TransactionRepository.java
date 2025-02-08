package com.kevinbank.accountbalancecalculation.repository;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountIdOrTargetAccountId(Long sourceAccountId, Long targetAccountId);
} 