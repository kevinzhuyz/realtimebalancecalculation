package com.kevinbank.accountbalancecalculation.mapper;

import com.kevinbank.accountbalancecalculation.domain.Transaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionMapper {
    
    @Select("SELECT * FROM transaction WHERE transaction_id = #{transactionId}")
    Transaction findByTransactionId(@Param("transactionId") String transactionId);
    
    @Select("SELECT * FROM transaction WHERE source_card_id = #{cardId} OR target_card_id = #{cardId}")
    List<Transaction> findByCardId(@Param("cardId") String cardId);
    
    @Select("SELECT * FROM transaction ORDER BY timestamp DESC")
    List<Transaction> findAll();
    
    @Insert("INSERT INTO transaction (transaction_id, amount, source_card_id, target_card_id, " +
            "timestamp, tran_type) " +
            "VALUES (#{transactionId}, #{amount}, #{sourceCardId}, #{targetCardId}, " +
            "#{timestamp}, #{tranType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Transaction transaction);
} 