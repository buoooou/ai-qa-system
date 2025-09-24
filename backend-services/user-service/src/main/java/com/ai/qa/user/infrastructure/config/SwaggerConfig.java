package com.ai.qa.user.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        /**
         * 配置当前微服务的API文档元信息
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                // 服务基本信息（会显示在文档中）
                                .info(new Info()
                                                .title("用户服务API文档") // 服务标题
                                                .description("提供用户相关的API接口") // 服务描述
                                                .version("v1.0.0") // 服务版本
                                );
        }
}
