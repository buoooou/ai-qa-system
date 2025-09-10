package com.ai.qa.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API 网关启动类
 * 负责启动网关服务，处理服务路由、认证等核心功能
 */
@SpringBootApplication
@EnableDiscoveryClient // 启用服务发现（用于从 Nacos 获取服务列表）
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
