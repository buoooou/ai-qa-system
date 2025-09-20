package com.ai.qa.qaservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ai.qa.qaservice.infrastructure.filter.GatewaySecretFilter;

import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig { // 不再继承WebSecurityConfigurerAdapter
    @Value("${gateway.secretId}")
    private String gatewaySecret;

    @Autowired
    private GatewaySecretFilter gatewaySecretFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 使用SecurityFilterChain Bean替代configure方法
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Lambda语法
                .csrf(csrf -> csrf.disable()) // Lambda语法
                .addFilterBefore(gatewaySecretFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth // 替换authorizeRequests为authorizeHttpRequests
                        .antMatchers("/api/qa/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex // Lambda语法
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("没有授权的访问路径: {}", request.getRequestURI());
                            response.setContentType("application/json;charset=UTF-8");
                            response.sendError(401, "Unauthorized");
                        }));

        return http.build();
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
