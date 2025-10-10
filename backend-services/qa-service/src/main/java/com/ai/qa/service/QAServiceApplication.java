package com.ai.qa.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * QA服务应用主类，负责启动和配置QA服务模块。
 * 启用Spring Boot自动配置、服务发现和Feign客户端支持。
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class QAServiceApplication {

    /**
     * 主方法，启动QA服务应用。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(QAServiceApplication.class, args);
    }
}
