package com.kevinbank.accountbalancecalculation.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器类，用于测试指标功能
 * 该类提供了Spring框架的RESTful Web服务，通过@RequestMapping注解定义了请求的基路径
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsTestController {

    /**
     * 测试指标的GET请求处理方法
     * 该方法被Timed注解标记，用于监控和记录请求的执行时间
     *
     * @return 返回一个字符串，表示指标测试端点的响应
     */
    @GetMapping("/test")
    @Timed(value = "metrics.test", description = "Time taken to test metrics")
    public String testMetrics() {
        return "Metrics test endpoint";
    }
}
