package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
} 