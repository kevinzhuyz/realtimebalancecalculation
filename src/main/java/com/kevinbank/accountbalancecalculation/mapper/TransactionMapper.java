/**
 * TransactionMapper接口用于定义交易对象与创建交易请求之间的映射关系
 * 它使用MapStruct库自动生成映射代码，以简化数据传输对象（DTO）与实体对象之间的转换过程
 */
package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.model.Transaction;
import com.kevinbank.accountbalancecalculation.model.CreateTransactionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 定义一个MapStruct映射器接口，用于将CreateTransactionRequest对象转换为Transaction对象
 * 该映射器被配置为使用Spring组件模型，这样它就可以作为Spring应用中的一个Bean自动被管理
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    /**
     * 将创建交易请求对象转换为交易对象
     * 此方法定义了两个特定的映射规则：
     * 1. 忽略Transaction对象的"id"字段，因为该值将在数据库中自动生成
     * 2. 设置"transactionTime"字段为当前系统时间，因为交易时间应为处理请求时的时间
     *
     * @param request 创建交易的请求对象，包含交易的相关信息
     * @return 返回一个根据CreateTransactionRequest对象生成的Transaction对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionTime", expression = "java(java.time.LocalDateTime.now())")
    Transaction toTransaction(CreateTransactionRequest request);

}
