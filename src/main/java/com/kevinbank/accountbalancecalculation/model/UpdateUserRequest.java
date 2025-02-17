package com.kevinbank.accountbalancecalculation.model;

import lombok.Data;
import com.kevinbank.accountbalancecalculation.entity.Gender;

@Data
public class UpdateUserRequest {
    private String name;
    private String password;  // 可选字段
    private Gender gender;
}