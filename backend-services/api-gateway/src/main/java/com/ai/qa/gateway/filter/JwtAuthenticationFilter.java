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
import org.springframework.http.ResponseCookie;

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
            if (path.contains("/api/users/login") || path.contains("/api/users/register") || path.contains("/api/users/checkNick")) {
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
                // 检查Token是否在本地存储中存在且有效
                // 这里我们不直接使用jwtUtil.validateToken，因为我们要先检查本地存储
                if (!jwtUtil.isTokenValidInStorage(token)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                // 检查Token是否过期
                if (jwtUtil.isTokenExpired(token)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                // 检查并处理Token续期
                String newToken = jwtUtil.renewTokenIfNeeded(token);
                
                // 构建修改后的请求
                ServerHttpRequest.Builder requestBuilder = request.mutate();
                
                // 将用户信息添加到请求头中
                requestBuilder.header("X-User-Id", jwtUtil.getUserIdFromToken(token))
                        .header("X-User-Name", jwtUtil.getUsernameFromToken(token));
                
                // 构建响应
                ServerHttpResponse response = exchange.getResponse();
                
                // 如果Token已续期，将新Token添加到响应中
                if (newToken != null && !newToken.equals(token)) {
                    // 在响应中设置新的Token
                    response.addCookie(ResponseCookie.from("token", newToken)
                            .httpOnly(true)
                            .path("/")
                            .build());
                    
                    // 也可以在响应头中添加新Token
                    response.getHeaders().add("X-New-Token", newToken);
                }

                return chain.filter(exchange.mutate().request(requestBuilder.build()).response(response).build());
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