package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis")
@Slf4j
public class RedisTestController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/test")
    public String testRedis() {
        String testKey = "test:key";
        String testValue = "Hello Redis!";
        
        try {
            log.info("Testing Redis operations...");
            
            // 测试写入
            log.info("Testing write operation");
            cacheService.set(testKey, testValue, 1, TimeUnit.MINUTES);
            
            // 测试读取（应该命中缓存）
            log.info("Testing read operation (should hit cache)");
            String cachedValue = cacheService.get(testKey, String.class);
            
            // 测试读取不存在的key（应该未命中缓存）
            log.info("Testing read operation with non-existent key (should miss cache)");
            String nonExistentValue = cacheService.get("non:existent:key", String.class);
            
            // 测试删除
            log.info("Testing delete operation");
            cacheService.delete(testKey);
            
            // 再次读取（应该未命中缓存）
            log.info("Testing read operation after delete (should miss cache)");
            String deletedValue = cacheService.get(testKey, String.class);
            
            return "Redis Test Complete - Initial read: " + 
                   (testValue.equals(cachedValue) ? "Hit" : "Miss") +
                   ", Non-existent read: " + (nonExistentValue == null ? "Miss" : "Hit") +
                   ", After delete read: " + (deletedValue == null ? "Miss" : "Hit");
                   
        } catch (Exception e) {
            log.error("Redis test failed", e);
            return "Redis Test Failed: " + e.getMessage();
        }
    }
}