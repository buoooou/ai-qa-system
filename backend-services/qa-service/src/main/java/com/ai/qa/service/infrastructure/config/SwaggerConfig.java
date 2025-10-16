package com.ai.qa.service.infrastructure.config;

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
                        .title("AI智能问答服务 QA-Service API")
                        .description("该NODE基于Google的Gemini大模型，提供与AI进行问答的服务。")
                        .version("1.0.0")
                        .contact(new Contact().email("sigefield.kircheis@gmail.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway"))
                .addServersItem(new Server()
                        .url("http://localhost:8082")
                        .description("QA-Service"));
    }
}
