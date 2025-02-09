package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisCacheServiceImplTest {

    @Autowired
    private RedisCacheServiceImpl cacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisConnection() {
        try {
            System.out.println("Starting Redis connection test...");
            System.out.println("Redis host: " + redisTemplate.getConnectionFactory().getConnection().getClientName());
            
            // 测试 Redis 连接
            Boolean result = redisTemplate.getConnectionFactory().getConnection().ping() != null;
            assertTrue(result);
            System.out.println("Redis connection test: " + (result ? "SUCCESS" : "FAILED"));
            
            // 测试基本操作
            String testKey = "test:connection";
            String testValue = "test";
            redisTemplate.opsForValue().set(testKey, testValue);
            String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
            System.out.println("Redis write/read test: " + (testValue.equals(retrievedValue) ? "SUCCESS" : "FAILED"));
            
            // 清理测试数据
            redisTemplate.delete(testKey);
            
        } catch (Exception e) {
            System.err.println("Redis connection error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testCacheOperations() {
        // 准备测试数据
        String testKey = "test:account:1";
        Account testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setBalance(new BigDecimal("1000.00"));

        // 测试存储
        cacheService.set(testKey, testAccount, 1, TimeUnit.MINUTES);

        // 测试读取
        Account cachedAccount = cacheService.get(testKey, Account.class);
        assertNotNull(cachedAccount);
        assertEquals(testAccount.getId(), cachedAccount.getId());
        assertEquals(testAccount.getBalance(), cachedAccount.getBalance());

        // 测试删除
        cacheService.delete(testKey);
        assertNull(cacheService.get(testKey, Account.class));
    }

    @Test
    void testCacheMetrics() {
        String testKey = "test:metrics";
        String testValue = "test";

        // 测试缓存未命中
        assertNull(cacheService.get(testKey, String.class));

        // 测试缓存写入
        cacheService.set(testKey, testValue, 1, TimeUnit.MINUTES);

        // 测试缓存命中
        assertEquals(testValue, cacheService.get(testKey, String.class));

        // 测试缓存删除后未命中
        cacheService.delete(testKey);
        assertNull(cacheService.get(testKey, String.class));
    }
} 