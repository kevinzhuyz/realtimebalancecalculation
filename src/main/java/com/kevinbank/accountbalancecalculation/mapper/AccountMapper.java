package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    Account toAccount(CreateAccountRequest createAccountRequest);
} 