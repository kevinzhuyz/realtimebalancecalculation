package com.kevinbank.accountbalancecalculation.service.impl;

import com.kevinbank.accountbalancecalculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.prometheus.client.Counter;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheServiceImpl implements CacheService {
    
    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private final Counter cacheHits = Counter.build()
        .name("redis_cache_hits_total")
        .help("Total number of cache hits")
        .register();
        
    private final Counter cacheMisses = Counter.build()
        .name("redis_cache_misses_total")
        .help("Total number of cache misses")
        .register();
    
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis set error: key={}", key, e);
        }
    }
    
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
    
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete error: key={}", key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
} 