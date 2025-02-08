package com.kevinbank.accountbalancecalculation.service;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.mapper.UserMapper;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
    
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}