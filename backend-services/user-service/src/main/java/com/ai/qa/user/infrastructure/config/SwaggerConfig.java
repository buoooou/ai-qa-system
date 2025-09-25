package com.ai.qa.user.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户服务 API")
                        .description("AI问答系统用户服务接口文档")
                        .version("1.0"));
    }
}