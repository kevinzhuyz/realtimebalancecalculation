package com.kevinbank.accountbalancecalculation.repository;

import com.kevinbank.accountbalancecalculation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库接口，用于执行用户相关的数据库操作
 * 继承了JpaRepository，自动获得了CRUD功能
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 检查数据库中是否存在指定名称的用户
     *
     * @param name 待检查的用户名
     * @return 如果存在指定名称的用户，则返回true；否则返回false
     */
    boolean existsByName(String name);
}
