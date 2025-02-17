package com.kevinbank.accountbalancecalculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * AccountbalancecalculationApplication 是账户余额计算应用的主类。
 * 使用SpringBoot框架进行开发，支持REST API接口。
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.kevinbank.accountbalancecalculation.repository")
@EnableCaching
@Slf4j
@Component
public class AccountbalancecalculationApplication {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @PostConstruct
    public void init() {
        log.info("Testing Redis connection at startup...");
        try {
            redisTemplate.opsForValue().set("test", "Hello Redis", 1, TimeUnit.MINUTES);
            String value = (String) redisTemplate.opsForValue().get("test");
            log.info("Redis test value: {}", value);
        } catch (Exception e) {
            log.error("Redis connection test failed", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountbalancecalculationApplication.class, args);
    }
}