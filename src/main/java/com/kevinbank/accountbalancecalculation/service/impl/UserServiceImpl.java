package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.mapper.UserMapper;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 创建新用户
     *
     * @param request 创建用户的请求数据，包含用户基本信息
     * @return 返回创建成功后的用户对象
     * @throws RuntimeException 如果用户名已存在，则抛出运行时异常
     */
    @Override
    @Transactional
    public User createUser(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByName(request.getName())) {
            throw new RuntimeException("用户名已存在");
        }

        // 使用 MapStruct 转换对象
        User user = userMapper.toUser(request);

        // 加密密码
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        try {
            // 保存用户
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            throw new RuntimeException("创建用户失败");
        }
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 返回找到的用户对象
     * @throws RuntimeException 如果用户不存在，则抛出运行时异常
     */
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 更新用户信息
     *
     * @param user 需要更新的用户对象
     * @return 返回更新后的用户对象
     * @throws RuntimeException 如果用户不存在，则抛出运行时异常
     */
    @Override
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("用户不存在");
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
    public void deleteUser(Long userId) {
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
