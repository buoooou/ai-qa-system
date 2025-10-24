package com.ai.qa.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // <--- 1. 导入注解

@SpringBootApplication
@EnableDiscoveryClient // (在新版中可选，但建议保留)
@EnableFeignClients // <--- 启用 Feign 客户端功能
public class QAServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QAServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("🤖 QA Service 启动成功!");
        System.out.println("📡 服务端口: 8082");
        System.out.println("🔗 健康检查: http://localhost:8082/api/qa/health");
        System.out.println("💬 问答接口: http://localhost:8082/api/qa/ask");
        System.out.println("📚 历史记录: http://localhost:8082/api/qa/history/{userId}");
        System.out.println("=================================");
    }
}
