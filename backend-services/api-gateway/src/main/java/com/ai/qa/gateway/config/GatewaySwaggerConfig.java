package com.ai.qa.gateway.config;

// @Configuration
public class GatewaySwaggerConfig {

    // @Bean
    // public GroupedOpenApi customGroupApiGateway() {
    //     return GroupedOpenApi.builder()
    //         .group("gateway")
    //         .pathsToMatch("/api/**")
    //         .build();
    // }

    // @Bean
    // public OpenAPI customOpenAPI() {
    //     return new OpenAPI()
    //             .info(new Info().title("API-Gateway").version("1.0.0")
    //                 .contact(new Contact().url("https://github.com/leihuang-c/ai-qa-system")))
    //             .addServersItem(new Server().url("/").description("API Gateway"))
    //             .addSecurityItem(new SecurityRequirement().addList("JWT"))
    //             .components(new Components()
    //                     .addSecuritySchemes("JWT", new SecurityScheme()
    //                             .type(SecurityScheme.Type.HTTP)
    //                             .scheme("bearer")
    //                             .bearerFormat("JWT"))
    //             )
    //             ;
    // }
}
