package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.entity.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 用户服务接口，提供用户相关的操作
 */
@Service
public interface UserService {
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @Cacheable(value = "users", key = "#userId")
    User getUserById(Long userId);

    /**
     * 创建新用户
     * @param request 创建用户请求
     * @return 创建的用户
     */
    @CachePut(value = "users", key = "#user.id")
    User createUser(CreateUserRequest request);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户
     */
    User updateUser(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @CacheEvict(value = "users", key = "#userId")
    void deleteUser(Long userId);

    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsById(Long userId);
}
