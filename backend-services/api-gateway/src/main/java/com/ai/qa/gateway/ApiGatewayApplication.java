package com.ai.qa.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ApiGatewayApplication {
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