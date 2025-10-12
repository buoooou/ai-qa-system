package com.ai.qa.service.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

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
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 优先尝试从当前请求上下文获取token
                String authHeader = getCurrentRequestAuthHeader();

                if (authHeader != null && !authHeader.isEmpty()) {
                    template.header("Authorization", authHeader);
                    System.out.println("Feign interceptor: Added Authorization header from request context for " + template.url());
                } else {
                    // 生成内部服务认证token
                    String internalToken = generateInternalServiceToken();
                    template.header("Authorization", "Bearer " + internalToken);
                    System.out.println("Feign interceptor: Added internal service token for " + template.url());
                }
            }
        };
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

    /**
     * 生成内部服务认证token
     */
    private String generateInternalServiceToken() {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(60 * 60 * 24); // 24小时

            return Jwts.builder()
                    .setIssuer(jwtIssuer)
                    .setSubject("qa-service")
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .claim("role", "INTERNAL_SERVICE")
                    .claim("service", "qa-service")
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("Error generating internal service token: " + e.getMessage());
            return "";
        }
    }
}