package com.ai.qa.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 问答服务应用启动类
 * 
 * AI智能问答服务的核心应用，负责：
 * 1. AI问答处理 - 集成Gemini API提供智能回答
 * 2. 对话历史管理 - 保存和查询用户问答记录
 * 3. 上下文维护 - 维持对话的连续性
 * 4. 服务注册 - 向Nacos注册中心注册服务
 * 5. 数据持久化 - 使用MySQL存储问答历史
 * 
 * 技术栈：
 * - Spring Boot: 微服务框架
 * - Spring Data JPA: 数据访问层
 * - MySQL: 关系型数据库
 * - Nacos: 服务注册与发现
 * - Gemini API: Google AI服务
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@SpringBootApplication      // Spring Boot应用标识
@EnableDiscoveryClient     // 启用服务发现客户端
@EnableFeignClients        // 启用Feign客户端
public class QAServiceApplication {
    
    /**
     * 应用程序入口点
     * 
     * @param args 命令行参数
     */
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
