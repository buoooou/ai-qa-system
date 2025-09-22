package com.ai.qa.user.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI文档配置类
 * 
 * 配置用户服务的API文档生成：
 * 1. API信息配置
 * 2. 联系信息配置
 * 3. 许可证配置
 * 
 * 访问地址：http://localhost:8081/swagger-ui.html
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * 创建OpenAPI配置
     * 
     * @return OpenAPI API文档配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI智能问答系统 - 用户服务API")
                        .description("用户管理相关的REST API接口文档")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Qiao Zhe")
                                .url("https://github.com/your-username")
                                .email("support@ai-qa-system.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8081")
                        .description("User Service Direct"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway"));
    }
}
