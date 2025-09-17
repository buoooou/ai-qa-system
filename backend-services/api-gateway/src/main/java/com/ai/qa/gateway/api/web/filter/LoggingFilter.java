package com.ai.qa.gateway.api.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 请求日志过滤器
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(FORMATTER);
        
        String method = request.getMethod().name();
        String url = request.getURI().getPath();
        String query = request.getURI().getQuery();
        String clientIp = getClientIp(request);
        
        log.info("=== 请求开始 ===");
        log.info("时间: {}", timestamp);
        log.info("方法: {}", method);
        log.info("URL: {}{}", url, query != null ? "?" + query : "");
        log.info("客户端IP: {}", clientIp);
        
        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                log.info("=== 请求结束 ===");
                log.info("响应状态: {}", response.getStatusCode());
                log.info("响应时间: {}ms", duration);
                log.info("==================");
            })
        );
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -200;
    }
}

