package com.ai.qa.gateway.infrastructure.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@EnableWebFluxSecurity // spring gateway, Enable Spring Security for WebFlux
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthFilter;

    // @Bean
    // public ReactiveAuthenticationManager reactiveAuthenticationManager() {
    // // Simple authentication manager that accepts any valid JWT authentication
    // return authentication -> Mono.just(
    // new UsernamePasswordAuthenticationToken(
    // authentication.getPrincipal(),
    // authentication.getCredentials(),
    // authentication.getAuthorities()));
    // }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // JwtAuthenticationWebFilter jwtAuthFilter = new
        // JwtAuthenticationWebFilter(reactiveAuthenticationManager());

        return http
                .cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/api/test/*", "/api/user/login", "/api/user/register")
                .permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}