package com.ai.qa.gateway.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

        /**
         * 配置网关自身的基础API文档信息（标题、描述等）
         */
        @Bean
        public OpenAPI gatewayOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("微服务API网关文档")
                                                .description("聚合所有微服务的接口文档，统一入口调试")
                                                .version("v1.0.0"));
        }

        /**
         * 聚合所有微服务的Swagger文档
         * 原理：通过网关的路由定义，动态发现微服务并生成对应的Swagger资源
         */
        @Bean
        public List<GroupedOpenApi> groupedOpenApis(RouteDefinitionLocator routeDefinitionLocator) {
                List<GroupedOpenApi> groups = new ArrayList<>();

                // 获取网关中定义的所有路由
                Flux<RouteDefinition> routeDefinitions = routeDefinitionLocator.getRouteDefinitions();

                // 遍历路由，为每个微服务创建一个Swagger分组
                routeDefinitions.toStream().forEach(route -> {
                        // 路由ID通常对应微服务名称（如：user-service、order-service）
                        String serviceName = route.getId();

                        // 微服务的API路径前缀（如：/api/user/** 对应的前缀为 /api/user）
                        String pathPrefix = extractPathPrefix(route);

                        // 创建分组：按微服务名称分组，只扫描对应路径的接口
                        GroupedOpenApi group = GroupedOpenApi.builder()
                                        .group(serviceName) // 分组名称（微服务名称）
                                        .pathsToMatch(pathPrefix + "/**") // 匹配该微服务的所有接口路径
                                        .build();
                        groups.add(group);
                });

                return groups;
        }

        /**
         * 从路由定义中提取微服务的API路径前缀
         * 例如：路由配置为 predicates: Path=/api/user/** ，则提取出 /api/user
         */
        private String extractPathPrefix(RouteDefinition route) {
                return route.getPredicates().stream()
                                .filter(predicate -> predicate.getName().equals("Path"))
                                .findFirst()
                                .map(predicate -> {
                                        String pathPattern = predicate.getArgs().get("pattern");
                                        // 移除通配符 **，得到前缀（如 /api/user/** → /api/user）
                                        return pathPattern.replace("/**", "");
                                })
                                .orElse("/");
        }

}
