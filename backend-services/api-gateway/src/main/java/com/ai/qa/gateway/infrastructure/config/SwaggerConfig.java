package com.ai.qa.gateway.infrastructure.config;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

        private final RouteDefinitionLocator routeDefinitionLocator;

        public SwaggerConfig(RouteDefinitionLocator routeDefinitionLocator) {
                this.routeDefinitionLocator = routeDefinitionLocator;
        }

        @Bean
        public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters) {
                List<GroupedOpenApi> groups = new ArrayList<>();

                // 从路由定义中自动发现服务
                List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions().collectList().block();
                if (definitions != null) {
                        definitions.forEach(routeDefinition -> {
                                String routeId = routeDefinition.getId();
                                if (routeId.contains("user-service")) {
                                        groups.add(GroupedOpenApi.builder().group("user-service")
                                                        .pathsToMatch("/user/**").build());
                                }
                                if (routeId.contains("qa-service")) {
                                        groups.add(GroupedOpenApi.builder().group("qa-service").pathsToMatch("/qa/**")
                                                        .build());
                                }
                        });
                }

                return groups;
        }
}
