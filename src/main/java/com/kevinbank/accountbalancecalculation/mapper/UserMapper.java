package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.entity.User;
import com.kevinbank.accountbalancecalculation.model.CreateUserRequest;
import com.kevinbank.accountbalancecalculation.model.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * UserMapper接口用于定义用户对象与创建用户请求之间的映射关系
 * 它使用MapStruct库自动生成映射代码，以简化用户创建过程中的数据转换
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * 将CreateUserRequest对象转换为User对象
     * 此方法忽略User对象的id、passwordHash和createdAt字段，以防止在创建过程中设置这些值
     *
     * @param createUserRequest 创建用户的请求对象，包含用户的基本信息
     * @return 返回一个User对象，其中id、passwordHash和createdAt字段未被设置
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    UserDTO toDTO(User user);

}
