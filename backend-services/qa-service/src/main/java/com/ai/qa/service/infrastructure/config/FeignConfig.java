package com.ai.qa.service.infrastructure.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Feign 客户端配置
 * 用于添加认证头等
 */
@Configuration
public class FeignConfig {

    @Value("${security.jwt.secret:7zmKiI4PToMYMC9G5nuAcL5JBRBYFZCzrn7PIM4zPZY=}")
    private String jwtSecret;

    @Value("${security.jwt.issuer:user-service-fyb}")
    private String jwtIssuer;

    /**
     * 为所有 Feign 请求添加 Authorization 头
     * 统一使用用户token，所有服务间调用都经过认证
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 从当前请求上下文获取用户token
                String authHeader = getCurrentRequestAuthHeader();

                if (authHeader != null && !authHeader.isEmpty()) {
                    template.header("Authorization", authHeader);
                    System.out.println("Feign interceptor: Added user Authorization header for " + template.url());
                } else {
                    System.out.println("Feign interceptor: No Authorization header found in context for " + template.url());
                    // 这里不生成任何token，确保所有调用都有用户token
                }
            }
        };
    }

    /**
     * Feign重试配置
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(5, Duration.ofSeconds(3).toMillis(), 3000);
    }

    /**
     * Feign请求配置
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            (int) Duration.ofSeconds(60).getSeconds(), // 连接超时
            (int) Duration.ofSeconds(120).getSeconds() // 读取超时
        );
    }

    /**
     * 获取当前请求的Authorization头
     */
    private String getCurrentRequestAuthHeader() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && !authHeader.isEmpty()) {
                    return authHeader;
                }
            }
        } catch (Exception e) {
            System.out.println("Feign interceptor: Error getting auth header: " + e.getMessage());
        }
        return null;
    }
}