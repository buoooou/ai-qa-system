package com.ai.qa.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Swagger3配置类 - 用户服务
 * 访问地址: http://localhost:8081/swagger-ui.html
 * 或通过网关访问: http://localhost:8080/api/user/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API文档元信息配置
                .info(new Info()
                        // 文档标题 - 明确标识为用户服务
                        .title("AI问答系统 - 用户服务API文档")
                        // 详细描述服务功能
                        .description("本API文档详细描述了AI问答系统中用户服务的所有接口，包括用户注册、登录、信息管理、认证授权等核心功能。\n" +
                                "用户服务负责系统用户的全生命周期管理，提供安全可靠的用户身份验证和授权机制。")
                        // 服务版本号
                        .version("1.0.0")
                        // 联系信息
                        .contact(new Contact()
                                .name("系统管理员")
                                .email("admin@ai-qa-system.com")
                                .url("http://ai-qa-system.com"))
                        // 许可证信息
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // 服务器环境配置
                .servers(java.util.Arrays.asList(
                        // 本地开发测试环境
                        new Server().url("http://localhost:8081/").description("本地开发测试环境 - 用户服务")
                        // 可根据需要添加其他环境配置
                        // new Server().url("http://test-api.ai-qa-system.com/").description("测试环境")
                        // new Server().url("http://api.ai-qa-system.com/").description("生产环境")
                ))
                // 安全认证配置 - JWT令牌
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT认证令牌，用于访问受保护的API资源。获取方式：通过登录接口获取令牌后，在Authorization头部添加Bearer {token}")));
    }

}
