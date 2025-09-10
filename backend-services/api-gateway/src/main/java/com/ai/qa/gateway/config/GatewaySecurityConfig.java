package com.ai.qa.gateway.config;

import com.ai.qa.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    // 构造函数注入需要添加Autowired注解，尤其在某些Spring版本中
    @Autowired
    public GatewaySecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());

        http.authorizeExchange(exchanges -> exchanges
                // 开放登录注册接口
                .pathMatchers("/api/auth/**").permitAll()
                // 开放Swagger文档接口
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**").permitAll()
                // 健康检查接口通常也需要开放
                .pathMatchers("/actuator/health/**").permitAll()
                // 其他接口需要认证
                .anyExchange().authenticated()
        );

        // 添加JWT过滤器
        http.addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}

