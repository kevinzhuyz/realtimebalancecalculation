package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionTime", expression = "java(java.time.LocalDateTime.now())")
    Transaction toTransaction(CreateTransactionRequest request);
    
    // 如果需要其他映射方法，可以在这里添加
} 