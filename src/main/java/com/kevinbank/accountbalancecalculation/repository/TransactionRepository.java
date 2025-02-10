package com.kevinbank.accountbalancecalculation.repository;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * TransactionRepository接口用于处理与Transaction实体相关的数据库操作.
 * 它继承自JpaRepository，从而获得对Transaction实体的基本CRUD操作支持.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 根据源账户ID或目标账户ID查找交易记录.
     * 此方法允许通过提供源账户ID或目标账户ID来检索相关的交易记录，
     * 以便用户可以查看特定账户的所有支出和收入交易.
     *
     * @param sourceAccountId  源账户ID
     * @param targetAccountId  目标账户ID
     * @return 包含所有匹配交易记录的列表
     */
    List<Transaction> findBySourceAccountIdOrTargetAccountId(Long sourceAccountId, Long targetAccountId);
}
