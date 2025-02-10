package com.kevinbank.accountbalancecalculation.repository;

import com.kevinbank.accountbalancecalculation.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

/**
 * AccountRepository接口扩展了JpaRepository，用于处理Account实体的数据库操作。
 * 它提供了几个定制的查询方法，以便于根据账户号码或用户ID检索账户信息。
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * 检查是否存在给定账户号码的账户。
     *
     * @param accountNumber 账户号码，用于查询。
     * @return 如果存在匹配的账户号码，则返回true；否则返回false。
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * 使用悲观写锁获取账户信息。
     * 这个方法主要用于处理并发事务，防止账户信息在读取过程中被修改。
     *
     * @param id 账户ID，用于查询。
     * @return 包含账户信息的Optional对象，如果找不到匹配的账户则返回Optional.empty()。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);

    /**
     * 根据账户号码查找账户信息。
     *
     * @param accountNumber 账户号码，用于查询。
     * @return 包含账户信息的Optional对象，如果找不到匹配的账户则返回Optional.empty()。
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * 根据用户ID查找所有账户信息。
     * 这个方法用于获取特定用户的所有账户列表。
     *
     * @param userId 用户ID，用于查询。
     * @return 包含账户信息的列表，如果找不到匹配的账户则返回空列表。
     */
    List<Account> findByUserId(Long userId);
}
