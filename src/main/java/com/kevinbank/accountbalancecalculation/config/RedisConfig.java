package com.kevinbank.accountbalancecalculation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 用于配置 RedisTemplate 以定义如何操作 Redis 数据库
 */
@Configuration
public class RedisConfig {

    /**
     * 创建并配置 RedisTemplate 实例
     *
     * @param connectionFactory Redis 连接工厂，用于创建 Redis 连接
     * @return 配置好的 RedisTemplate 实例，用于操作 Redis 数据库
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 JSON 序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 设置 key 和 value 的序列化方式
        // 使用 StringRedisSerializer 来序列化 key，以确保 key 是人类可读的字符串格式
        // 使用 GenericJackson2JsonRedisSerializer 来序列化 value，以支持复杂对象的存储
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);

        // 初始化 RedisTemplate
        template.afterPropertiesSet();
        return template;
    }
}
