package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.prometheus.client.Counter;
import javax.annotation.PostConstruct;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Set;

/**
 * 使用Redis实现缓存服务的实现类
 */
@Service
public class RedisCacheServiceImpl implements CacheService {

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

    // Redis模板，用于操作Redis缓存
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Prometheus计数器，用于统计缓存命中次数
    private final Counter cacheHits = Counter.build()
        .name("redis_cache_hits_total")
        .help("Total number of cache hits")
        .register();

    // Prometheus计数器，用于统计缓存未命中次数
    private final Counter cacheMisses = Counter.build()
        .name("redis_cache_misses_total")
        .help("Total number of cache misses")
        .register();

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
            log.info("Attempting to write to Redis - Key: {}, Value type: {}", key, value.getClass().getName());
            if (key == null || value == null) {
                log.error("Cannot set cache with null key or value. Key: {}, Value: {}", key, value);
                return;
            }
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            // 立即验证写入
            Object savedValue = redisTemplate.opsForValue().get(key);
            if (savedValue != null) {
                log.info("Verification after write - Key: {}, Saved Value type: {}", key, savedValue.getClass().getName());
            } else {
                log.warn("Verification failed - No value found for key: {}", key);
            }
        } catch (Exception e) {
            log.error("Redis set error: key={}, value type={}, error={}", 
                key, value.getClass().getName(), e.getMessage(), e);
            // 打印更详细的异常堆栈
            e.printStackTrace();
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
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            log.info("Attempting to get from Redis - Key: {}, Type: {}", key, type.getName());
            if (key == null) {
                log.error("Cannot get cache with null key");
                return null;
            }
            Object value = redisTemplate.opsForValue().get(key);
            log.info("Retrieved from Redis - Key: {}, Value: {}", key, value);
            if (value != null) {
                log.debug("Cache hit for key: {}", key);
                cacheHits.inc();
                if (!type.isInstance(value)) {
                    log.error("Type mismatch for key: {}. Expected: {}, Got: {}", 
                        key, type.getName(), value.getClass().getName());
                    return null;
                }
                return (T) value;
            } else {
                log.debug("Cache miss for key: {}", key);
                cacheMisses.inc();
                return null;
            }
        } catch (Exception e) {
            log.error("Redis get error: key={}, type={}, error={}", key, type.getName(), e.getMessage(), e);
            cacheMisses.inc();
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
            log.debug("Attempting to delete cache for key: {}", key);
            if (key == null) {
                log.error("Cannot delete cache with null key");
                return;
            }
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                log.debug("Successfully deleted cache for key: {}", key);
            } else {
                log.warn("Key not found for deletion: {}", key);
            }
        } catch (Exception e) {
            log.error("Redis delete error: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Failed to delete cache", e);
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
            log.debug("Checking existence of key: {}", key);
            if (key == null) {
                log.error("Cannot check existence of null key");
                return false;
            }
            Boolean exists = redisTemplate.hasKey(key);
            log.debug("Key {} exists: {}", key, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Redis exists check error: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    @PostConstruct
    public void init() {
        try {
            log.info("Testing Redis connection...");
            String testKey = "test:connection";
            String testValue = "test";
            set(testKey, testValue, 5, TimeUnit.SECONDS);
            Object result = get(testKey, String.class);
            if (testValue.equals(result)) {
                log.info("Redis connection test successful");
            } else {
                log.error("Redis connection test failed: value mismatch");
            }
        } catch (Exception e) {
            log.error("Redis connection test failed", e);
        }
    }
}
