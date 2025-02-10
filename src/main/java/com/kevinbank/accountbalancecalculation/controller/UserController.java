package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * UserController类负责处理与用户相关的HTTP请求，提供用户创建、查询、更新和删除的功能
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 自动注入UserService，用于执行用户相关的业务逻辑操作
     */
    @Autowired
    private UserService userService;

    /**
     * 创建新用户
     *
     * @param request 包含用户创建信息的请求体
     * @return 新创建的用户对象
     */
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 对应ID的用户对象，如果不存在则返回null
     */
    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID，用于标识需要更新的用户
     * @param user 包含更新信息的用户对象
     * @return 更新后的用户对象
     */
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User user) {
        user.setId(userId);
        return userService.updateUser(user);
    }

    /**
     * 删除指定用户
     *
     * @param userId 用户ID，用于标识需要删除的用户
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
