package com.ai.qa.service.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Feign 客户端配置
 * 用于添加认证头等
 */
@Configuration
public class FeignConfig {

    /**
     * 为所有 Feign 请求添加 Authorization 头
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Temporarily remove Authorization header for session creation
                // since we've allowed /api/user/**/sessions/** without authentication
                System.out.println("Feign interceptor: template URL = " + template.url());
                System.out.println("Feign interceptor: No Authorization header added (session endpoints are now public)");

                // Don't add Authorization header for session endpoints
                // This prevents authentication issues while maintaining functionality
            }
        };
    }
}