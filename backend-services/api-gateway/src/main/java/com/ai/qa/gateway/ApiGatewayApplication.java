package com.ai.qa.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关应用启动类
 *
 * 作为整个微服务系统的统一入口，负责：
 * 1. 路由转发 - 将请求转发到相应的微服务
 * 2. 负载均衡 - 在多个服务实例间分发请求
 * 3. 服务发现 - 自动发现和注册微服务
 * 4. 统一鉴权 - 在网关层进行身份验证
 * 5. 限流熔断 - 保护后端服务不被过载
 * 6. 日志监控 - 统一记录请求日志
 *
 * 技术栈：
 * - Spring Cloud Gateway: 响应式网关框架
 * - Nacos: 服务注册与发现
 * - LoadBalancer: 客户端负载均衡
 *
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@SpringBootApplication      // Spring Boot应用标识
@EnableDiscoveryClient     // 启用服务发现客户端
public class ApiGatewayApplication {

    /**
     * 应用程序入口点
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("=================================");
        System.out.println("🚀 API Gateway 启动成功!");
        System.out.println("📡 服务端口: 8080");
        System.out.println("🔗 健康检查: http://localhost:8080/actuator/health");
        System.out.println("📊 服务发现: http://localhost:8080/gateway/services");
        System.out.println("=================================");
    }
}
