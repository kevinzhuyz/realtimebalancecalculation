package com.kevinbank.accountbalancecalculation.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsTestController {

    @GetMapping("/test")
    @Timed(value = "metrics.test", description = "Time taken to test metrics")
    public String testMetrics() {
        return "Metrics test endpoint";
    }
} 