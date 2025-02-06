package com.kevinbank.accountbalancecalculation;

import org.springframework.boot.SpringApplication;

public class TestAccountBalanceCalculationApplication {

    public static void main(String[] args) {
        SpringApplication.from(AccountbalancecalculationApplication::main).with(TestContainersConfiguration.class).run(args);
    }

}
