package com.kevinbank.accountbalancecalculation.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

@Component
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;
    private static final Logger log = LoggerFactory.getLogger(RedisHealthIndicator.class);

    public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pingResponse = connection.ping();
            log.info("Redis ping result: {}", pingResponse);
            
            if ("PONG".equals(pingResponse)) {
                Properties info = connection.info();
                String version = info.getProperty("redis_version");
                log.info("Redis is up, version: {}", version);
                return Health.up()
                    .withDetail("version", version)
                    .withDetail("status", "connected")
                    .build();
            }
            log.warn("Redis server not responding properly");
            return Health.down()
                .withDetail("error", "Redis server not responding")
                .withDetail("ping_result", pingResponse)
                .build();
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return Health.down(e).build();
        }
    }
} 