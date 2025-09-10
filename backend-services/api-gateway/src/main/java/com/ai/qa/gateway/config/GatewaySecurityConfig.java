package com.ai.qa.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import com.ai.qa.gateway.common.JwtReactiveAuthenticationManager;
import com.ai.qa.gateway.common.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class GatewaySecurityConfig {

    private final JwtUtil jwtUtil;

    public GatewaySecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        log.debug("[API-Gateway] [{}]## {} Start.}", this.getClass().getSimpleName(), "securityWebFilterChain");
        http.csrf(csrf -> csrf.disable()
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/user/**").permitAll()
                        .pathMatchers("/api/qa/**").authenticated()
                        .anyExchange().authenticated())
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION));

        return http.build();
    }

    private AuthenticationWebFilter jwtAuthenticationFilter() {
        return new AuthenticationWebFilter(new JwtReactiveAuthenticationManager(jwtUtil));
    }

}
