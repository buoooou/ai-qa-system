package com.ai.qa.gateway.infrastructure.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.ai.qa.gateway.infrastructure.filter.JwtAuthenticationWebFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity // 适用于Spring Gateway的WebFlux安全配置
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationWebFilter jwtAuthFilter;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                // 使用Lambda风格配置请求授权
                                .authorizeExchange(exchanges -> exchanges
                                                // 放行所有OPTIONS请求
                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                // 放行无需认证的API路径
                                                .pathMatchers("/api/test/**",
                                                                "/api/user/login",
                                                                "/api/user/register")
                                                .permitAll()
                                                // 放行OpenAPI文档路径
                                                .pathMatchers("/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()
                                                // 放行Actuator健康检查路径
                                                .pathMatchers("/actuator/**")
                                                .permitAll()
                                                // 其他所有请求需要认证
                                                .anyExchange().authenticated())
                                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                                .build();
        }

        // 只有Gateway微服务中，需要配置跨域请求。其他微服务，不需要
        // 允许来自http://localhost:3000的跨域请求
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList("http://*:3000"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
