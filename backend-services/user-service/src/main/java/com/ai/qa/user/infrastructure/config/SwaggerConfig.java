package com.ai.qa.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI智能问答服务 User-Service API")
                        .description("该NODE实现用户相关的操作，包括用户注册·用户登录。")
                        .version("1.0.0")
                        .contact(new Contact().email("sigefield.kircheis@gmail.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway"))
                .addServersItem(new Server()
                        .url("http://localhost:8081")
                        .description("User-Service"));
    }
}
