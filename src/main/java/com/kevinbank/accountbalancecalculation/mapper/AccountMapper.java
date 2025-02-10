package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.model.Account;
import com.kevinbank.accountbalancecalculation.model.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * AccountMapper接口用于定义账户相关对象的映射关系
 * 它使用了MapStruct库来自动转换对象类型
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

    /**
     * 将创建账户请求转换为账户对象
     * 此方法定义了如何将CreateAccountRequest对象映射到Account对象
     * 它忽略了Account对象的id和createdAt字段，并将balance字段的默认值设置为0
     *
     * @param request 创建账户的请求对象，包含初始化账户所需的信息
     * @return 返回一个映射后的Account对象，其中balance字段被初始化为0，其他字段根据request进行映射
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "balance", source = "balance", defaultValue = "0")
    Account toAccount(CreateAccountRequest request);
}
