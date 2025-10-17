package com.ai.qa.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * 负载均衡配置类
 * 
 * 配置Spring Cloud LoadBalancer的负载均衡策略
 * 解决API Gateway路由到后端服务的负载均衡问题
 * 
 */
@Configuration
public class LoadBalancerConfig {
    // 使用默认的负载均衡配置
    // Spring Cloud Gateway会自动配置负载均衡器
}
