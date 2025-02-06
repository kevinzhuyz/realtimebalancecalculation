package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.domain.Account;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {
    
    @Select("SELECT * FROM account WHERE card_id = #{cardId}")
    Account findById(@Param("cardId") Long cardId);
    
    @Select("SELECT * FROM account")
    List<Account> findAll();
    
    @Insert("INSERT INTO account (user_id, amount, balance, credit_limit, account_number) " +
            "VALUES (#{userId}, #{amount}, #{balance}, #{creditLimit}, #{accountNumber})")
    @Options(useGeneratedKeys = true, keyProperty = "cardId")
    int insert(Account account);
    
    @Update("UPDATE account SET balance = #{balance} WHERE card_id = #{cardId}")
    int updateBalance(@Param("cardId") Long cardId, @Param("balance") BigDecimal balance);
    
    @Delete("DELETE FROM account WHERE card_id = #{cardId}")
    int deleteById(@Param("cardId") Long cardId);
} 