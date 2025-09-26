package com.ai.qa.user.infrastructure.config;

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
import com.ai.qa.user.infrastructure.filter.GatewaySecretFilter;

import lombok.extern.slf4j.Slf4j;

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
                .csrf(csrf -> csrf.disable()) // Lambda语法
                .addFilterBefore(gatewaySecretFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth // Lambda语法
                        // 放行无需认证的API路径
                        .antMatchers("/api/user/register",
                                "/api/user/login",
                                "/api/user/getUserName")
                        .permitAll()
                        // 放行OpenAPI文档路径
                        .antMatchers("/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**")
                        .permitAll()
                        // 放行Actuator健康检查路径
                        .antMatchers("/actuator/**")
                        .permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("没有授权的访问路径: {}", request.getRequestURI());
                            response.setContentType("application/json;charset=UTF-8");
                            response.sendError(401, "Unauthorized");
                        }));

        return http.build();
    }

}
