package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.repository.UserRepository;
import com.kevinbank.accountbalancecalculation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);  // 使用 Long 类型
        testUser.setName("testUser");
        testUser.setPasswordHash("hashedPassword");
        testUser.setGender("M");
        testUser = userRepository.save(testUser);

        createRequest = new CreateUserRequest();
        createRequest.setName("newUser");
        createRequest.setPassword("password");
        createRequest.setGender("F");
    }

    @Test
    void createUser() {
        User user = userService.createUser(createRequest);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(createRequest.getName(), user.getName());
        assertEquals(createRequest.getGender(), user.getGender());
    }

    @Test
    void getUserById() {
        User user = userService.getUserById(testUser.getId());
        assertNotNull(user);
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getName(), user.getName());
    }
} 