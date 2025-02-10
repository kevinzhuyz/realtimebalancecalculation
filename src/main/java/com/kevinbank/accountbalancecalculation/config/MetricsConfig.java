package com.kevinbank.accountbalancecalculation.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置Metrics度量监控的类
 * 该类定义了如何在Spring应用中集成Prometheus作为Metrics的后端
 */
@Configuration
public class MetricsConfig {

    /**
     * 创建并配置MeterRegistry实例
     * MeterRegistry是Micrometer库中用于收集度量数据的核心接口
     * 这里我们返回一个PrometheusMeterRegistry实例，使用默认的Prometheus配置
     *
     * @return PrometheusMeterRegistry实例，用于度量数据的收集
     */
    @Bean
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    /**
     * 创建并配置TimedAspect实例
     * TimedAspect用于自动记录方法的执行时间，通过接收MeterRegistry实例来存储度量数据
     *
     * @param registry MeterRegistry实例，用于度量数据的收集
     * @return TimedAspect实例，用于自动记录方法执行时间
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
