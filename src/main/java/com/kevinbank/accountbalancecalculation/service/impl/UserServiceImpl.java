package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.entity.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.mapper.UserMapper;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kevinbank.accountbalancecalculation.service.CacheService;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheService cacheService;

    private static final String USER_CACHE_KEY_PREFIX = "user:";
    private static final long CACHE_TIMEOUT = 30; // 缓存30分钟

    /**
     * 创建新用户
     *
     * @param request 创建用户的请求数据，包含用户基本信息
     * @return 返回创建成功后的用户对象
     * @throws RuntimeException 如果用户名已存在，则抛出运行时异常
     */
    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.id", unless = "#result == null")
    public User createUser(CreateUserRequest request) {
        log.debug("Creating user with request: {}", request);
        if (userRepository.existsByName(request.getName())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 返回找到的用户对象
     * @throws RuntimeException 如果用户不存在，则抛出运行时异常
     */
    @Override
    @Cacheable(value = "users", key = "#userId", unless = "#result == null")
    public User getUserById(Long userId) {
        log.debug("Getting user by id: {}", userId);
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    /**
     * 更新用户信息
     *
     * @param user 需要更新的用户对象
     * @return 返回更新后的用户对象
     * @throws RuntimeException 如果用户不存在，则抛出运行时异常
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        return userRepository.save(user);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @throws RuntimeException 如果用户不存在，则抛出运行时异常
     */
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void deleteUser(Long userId) {
        log.debug("Deleting user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }

    /**
     * 检查用户是否存在
     *
     * @param userId 用户ID
     * @return 如果用户存在返回true，否则返回false
     */
    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
