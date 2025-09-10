package com.ai.qa.user.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "JWT认证示例API",
                version = "1.0",
                description = "使用Spring Security和JJWT实现的认证授权示例API文档"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "开发服务器"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT认证令牌",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    // 配置自动生成，无需额外代码
}