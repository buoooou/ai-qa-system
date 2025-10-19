package com.ai.qa.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 全局日志过滤器
 * 
 * 记录所有通过API Gateway的请求信息
 * 帮助调试路由和负载均衡问题
 * 
 */
@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered  {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 记录请求信息
        log.info("🚀 Gateway Request: {} {} from {}", 
                request.getMethod(), 
                request.getURI(), 
                request.getRemoteAddress());
        
        // 记录请求头信息
        log.info("📋 Request Headers: {}", request.getHeaders());
        
        // 继续处理请求
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 记录响应信息
            log.info("✅ Gateway Response: {} for {} {}", 
                    exchange.getResponse().getStatusCode(),
                    request.getMethod(), 
                    request.getURI());
        }));
    }

    @Override
    public int getOrder() {
        // 设置为最高优先级，确保最先执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
