package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.prometheus.client.Counter;

import java.util.concurrent.TimeUnit;

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
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis set error: key={}", key, e);
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
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                cacheHits.inc();
                return (T) value;
            } else {
                cacheMisses.inc();
                return null;
            }
        } catch (Exception e) {
            log.error("Redis get error: key={}", key, e);
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
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete error: key={}", key, e);
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
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
