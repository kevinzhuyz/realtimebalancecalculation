package com.kevinbank.accountbalancecalculation.controller;

import com.kevinbank.accountbalancecalculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Redis测试控制器，用于验证Redis缓存操作的正确性
 */
@RestController
@RequestMapping("/api/redis")
@Slf4j
public class RedisTestController {

    /**
     * 注入缓存服务，用于执行Redis操作
     */
    @Autowired
    private CacheService cacheService;

    /**
     * 测试Redis缓存操作的端点
     * 包括设置、获取和删除缓存项
     * @return 返回测试结果的字符串
     */
    @GetMapping("/test")
    public String testRedis() {
        // 定义测试使用的键值对
        String testKey = "test:key";
        String testValue = "Hello Redis!";

        try {
            // 开始测试Redis操作
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

            // 返回测试结果
            return "Redis Test Complete - Initial read: " +
                   (testValue.equals(cachedValue) ? "Hit" : "Miss") +
                   ", Non-existent read: " + (nonExistentValue == null ? "Miss" : "Hit") +
                   ", After delete read: " + (deletedValue == null ? "Miss" : "Hit");

        } catch (Exception e) {
            // 记录测试中的错误
            log.error("Redis test failed", e);
            return "Redis Test Failed: " + e.getMessage();
        }
    }
}
