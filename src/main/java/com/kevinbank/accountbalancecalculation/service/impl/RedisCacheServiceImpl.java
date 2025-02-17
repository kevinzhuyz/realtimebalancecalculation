package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 使用Redis实现缓存服务的实现类
 */
@Service
public class RedisCacheServiceImpl implements CacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

    // Redis模板，用于操作Redis缓存
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存项
     *
     * @param key 缓存项的键
     * @param value 缓存项的值
     * @param timeout 缓存项的过期时间
     * @param unit 缓存项过期时间的时间单位
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            log.debug("Setting cache - Key: {}, Value: {}, Timeout: {} {}", 
                key, value, timeout, unit);
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            
            // 验证写入
            Object savedValue = redisTemplate.opsForValue().get(key);
            if (savedValue != null) {
                log.debug("Cache write verified - Key: {}, Value: {}", key, savedValue);
            } else {
                log.warn("Cache write verification failed - Key: {}", key);
            }
        } catch (Exception e) {
            log.error("Failed to set cache - Key: {}, Error: {}", key, e.getMessage(), e);
        }
    }

    /**
     * 获取缓存项
     *
     * @param key 缓存项的键
     * @param type 缓存项的类型
     * @param <T> 泛型方法参数，表示缓存项的类型
     * @return 缓存项的值，如果不存在则返回null
     */
    @Override
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return type.cast(value);
        } catch (Exception e) {
            log.error("Redis get error: key={}, type={}, error={}", 
                key, type.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存项
     *
     * @param key 缓存项的键
     */
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete error: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 检查缓存项是否存在
     *
     * @param key 缓存项的键
     * @return 如果缓存项存在则返回true，否则返回false
     */
    @Override
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis exists check error: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    @PostConstruct
    public void init() {
        try {
            String testKey = "test:connection";
            String testValue = "test";
            set(testKey, testValue, 1, TimeUnit.MINUTES);
            String getValue = get(testKey, String.class);
            
            if (testValue.equals(getValue)) {
                log.info("Redis connection test successful");
            } else {
                log.error("Redis connection test failed: value mismatch");
            }
        } catch (Exception e) {
            log.error("Redis connection test failed", e);
        }
    }
}
