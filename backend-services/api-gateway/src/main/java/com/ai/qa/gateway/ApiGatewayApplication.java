package com.ai.qa.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ApiGatewayApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ApiGatewayApplication.class, args);
        String jwtSecret = ctx.getEnvironment().getProperty("jwt.secret");
        System.out.println("JWT Secret: " + jwtSecret);
    }
}