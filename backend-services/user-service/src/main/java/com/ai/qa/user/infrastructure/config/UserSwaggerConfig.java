package com.ai.qa.user.infrastructure.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @io.swagger.v3.oas.annotations.info.Info(title = "User Service API", version = "1.0"),
    servers = @io.swagger.v3.oas.annotations.servers.Server(url = "/user-service", description = "User Service")
)
public class UserSwaggerConfig {

    @Bean
    public GroupedOpenApi customUserServiceGroupOpenAPI() {
        return GroupedOpenApi.builder()
            .group("user-service")
            .pathsToMatch("/api/users/**")
            .packagesToScan("com.ai.qa.user")
            .build();
    }

    @Bean
    public OpenAPI customUserServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("User Service API").version("1.0"))
            .addServersItem(new Server().url("/user-service").description("User Service"));
    }
}
