package com.ai.qa.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // <--- 1. 导入注解

@SpringBootApplication// <--- 启用 Feign 客户端功能
public class QAServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QAServiceApplication.class, args);
    }
}
