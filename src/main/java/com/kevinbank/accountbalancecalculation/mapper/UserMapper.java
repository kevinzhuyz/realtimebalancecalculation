package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.model.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toUser(CreateUserRequest createUserRequest);
    
    // Add any other mapping methods you need
} 