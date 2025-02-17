package com.kevinbank.accountbalancecalculation.model;

import lombok.Data;
import java.time.LocalDateTime;
import com.kevinbank.accountbalancecalculation.entity.Gender;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private Gender gender;
    private LocalDateTime createdAt;
    // 注意：不包含passwordHash
} 