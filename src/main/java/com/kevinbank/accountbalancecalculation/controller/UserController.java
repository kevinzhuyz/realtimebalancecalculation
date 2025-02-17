package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.entity.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.model.UpdateUserRequest;
import com.kevinbank.accountbalancecalculation.model.UserDTO;
import com.kevinbank.accountbalancecalculation.service.UserService;
import com.kevinbank.accountbalancecalculation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * UserController类负责处理与用户相关的HTTP请求，提供用户创建、查询、更新和删除的功能
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    /**
     * 自动注入UserService，用于执行用户相关的业务逻辑操作
     */
    @Autowired
    private UserService userService;

    /**
     * 自动注入PasswordEncoder，用于对密码进行编码
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建新用户
     *
     * @param request 包含用户创建信息的请求体
     * @return 新创建的用户对象
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.ok(userMapper.toDTO(user));
        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw e;
        }
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 对应ID的用户对象，如果不存在则返回null
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID，用于标识需要更新的用户
     * @param request 包含更新信息的用户对象
     * @return 更新后的用户对象
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        User existingUser = userService.getUserById(userId);
        
        existingUser.setName(request.getName());
        existingUser.setGender(request.getGender());
        
        // 如果提供了新密码，则更新密码
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            existingUser.setPasswordHash(encodedPassword);
        }
        
        User updatedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    /**
     * 删除指定用户
     *
     * @param userId 用户ID，用于标识需要删除的用户
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
