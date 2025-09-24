package com.ai.qa.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 * 配置API文档的基本信息
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI智能问答服务 API")
                        .description("基于Google Gemini AI的智能问答服务接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AI QA Team")
                                .email("support@ai-qa.com")
                                .url("https://ai-qa.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8082")
                        .description("QA Service Direct"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway"));
    }
}
