package com.kevinbank.accountbalancecalculation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类，用于定义密码编码策略
 */
@Configuration
public class SecurityConfig {

    /**
     * 创建并返回一个BCryptPasswordEncoder实例
     * BCryptPasswordEncoder是一种强大的密码编码器，用于对用户密码进行加密和比对
     * 使用BCrypt算法可以有效保护用户密码安全，防止数据泄露
     *
     * @return PasswordEncoder实例，用于密码加密和验证
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
