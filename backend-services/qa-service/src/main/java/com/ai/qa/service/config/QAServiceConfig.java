package com.ai.qa.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class QAServiceConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Q&A System - QA Service API")
                        .description("问答服务模块API文档，提供AI问答、历史记录查询等功能")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AI Q&A Team")
                                .email("support@aiqa.com"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
