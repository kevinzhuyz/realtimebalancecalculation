package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private CreateUserRequest mockRequest;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("testUser");
        mockUser.setGender("M");

        mockRequest = new CreateUserRequest();
        mockRequest.setName("testUser");
        mockRequest.setPassword("password123");
        mockRequest.setGender("M");
    }

    @Test
    void createUser_ShouldReturnUser() {
        // 准备
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(mockUser);

        // 执行
        ResponseEntity<User> response = userController.createUser(mockRequest);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void getUser_ShouldReturnUser() {
        // 准备
        when(userService.getUserById(1)).thenReturn(mockUser);

        // 执行
        ResponseEntity<User> response = userController.getUser(1);

        // 验证
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());
    }
} 