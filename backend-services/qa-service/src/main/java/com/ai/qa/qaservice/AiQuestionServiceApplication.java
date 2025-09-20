package com.ai.qa.qaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient // 启用Nacos服务发现
@EnableFeignClients
public class AiQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiQuestionServiceApplication.class, args);
    }

}
