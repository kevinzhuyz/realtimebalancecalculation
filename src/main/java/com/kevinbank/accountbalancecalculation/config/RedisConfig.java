package com.kevinbank.accountbalancecalculation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;

/**
 * Redis 配置类
 * 用于配置 RedisTemplate 以定义如何操作 Redis 数据库
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * 创建并配置 RedisTemplate 实例
     *
     * @param connectionFactory Redis 连接工厂，用于创建 Redis 连接
     * @return 配置好的 RedisTemplate 实例，用于操作 Redis 数据库
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.debug("Initializing Redis Template");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用配置好的ObjectMapper
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper());
        
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        
        // 测试连接并打印详细日志
        try {
            template.opsForValue().set("test:connection", "test", 1, TimeUnit.MINUTES);
            Object testValue = template.opsForValue().get("test:connection");
            log.info("Redis connection test - Test value: {}", testValue);
            
            if (testValue == null) {
                log.error("Redis test failed - Unable to retrieve test value");
            } else if (!"test".equals(testValue.toString())) {
                log.error("Redis test failed - Value mismatch. Expected 'test', got '{}'", testValue);
            } else {
                log.info("Redis connection test successful");
            }
        } catch (Exception e) {
            log.error("Redis connection test failed", e);
        }
        
        return template;
    }
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(0);
        
        // 只有在密码不为空时才设置密码
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());  // 添加Java 8时间模块支持
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 使用ISO-8601格式
        return mapper;
    }

    @PostConstruct
    public void testRedisConnection() {
        try {
            RedisTemplate<String, Object> template = redisTemplate(redisConnectionFactory());
            template.opsForValue().set("test:connection", "test", 1, TimeUnit.MINUTES);
            Object testValue = template.opsForValue().get("test:connection");
            log.info("Redis connection test - Test value: {}", testValue);
        } catch (Exception e) {
            log.error("Redis connection test failed", e);
        }
    }
}
