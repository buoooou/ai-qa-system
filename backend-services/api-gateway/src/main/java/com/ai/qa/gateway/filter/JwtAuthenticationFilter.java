package com.ai.qa.gateway.filter;

import com.ai.qa.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 检查是否是登录或注册请求，这些请求不需要Token验证
            String path = request.getURI().getPath();
            if (path.contains("/api/user/login") || path.contains("/api/user/register")) {
                return chain.filter(exchange);
            }

            // 从请求头中获取Authorization
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // 检查Authorization头是否存在
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            // 提取JWT Token
            String token = authHeader.substring(7);

            try {
                // 验证Token
                Claims claims = jwtUtil.validateToken(token);
                
                // 检查Token是否过期
                if (jwtUtil.isTokenExpired(token)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                // 将用户信息添加到请求头中
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", claims.get("userId", String.class))
                        .header("X-User-Name", claims.get("username", String.class))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        };
    }

    public static class Config {
        // 过滤器配置
    }
}