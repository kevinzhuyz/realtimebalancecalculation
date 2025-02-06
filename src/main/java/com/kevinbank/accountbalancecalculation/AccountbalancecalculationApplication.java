package com.kevinbank.accountbalancecalculation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * AccountbalancecalculationApplication 是账户余额计算应用的主类。
 * 使用SpringBoot框架进行开发，支持REST API接口。
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.kevinbank.accountbalancecalculation.mapper")
public class AccountbalancecalculationApplication {
    
    /**
     * 应用程序的入口方法。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AccountbalancecalculationApplication.class, args);
    }
}


