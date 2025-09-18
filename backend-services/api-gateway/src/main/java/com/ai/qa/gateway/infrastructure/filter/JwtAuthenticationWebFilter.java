package com.ai.qa.gateway.infrastructure.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${gateway.secretId}")
    private String gatewaySecret;

    public JwtAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
        // 授权JwtAuthenticationWebFilter的filter，对下面的路径（全路径）进行拦截
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String uri = request.getURI().getPath();
        log.info("========== JwtAuthenticationWebFilter path: {}", uri);
        log.info("jwtSecret: {}", jwtSecret);
        log.info("gatewaySecret: {}", gatewaySecret);

        // 前端资产以及后端注册登录的场合，直接放行
        if (uri.equals("/") ||
                uri.equals("/index.html") ||
                uri.startsWith("/static/") ||
                uri.startsWith("/assets/") ||
                uri.endsWith(".js") ||
                uri.endsWith(".css") ||
                uri.endsWith(".ico") ||
                uri.endsWith(".png") ||
                uri.contains("/api/test/") ||
                uri.contains("/register") ||
                uri.contains("/login")) {
            log.info("========== JwtAuthenticationFilter skip for path: {}", uri);

            // 构建新请求并添加X-Gateway-Secret头
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Gateway-Secret", gatewaySecret)
                    .build();

            // 放行
            // return chain.filter(exchange); // 放行
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        // 从请求头获取Token
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("========== Missing or invalid Authorization header");
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");

        }

        String token = authHeader.substring(7);
        try {
            // 验证JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 设置认证信息到上下文
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, null);

            // 将用户信息添加到请求头传递给下游服务
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-Gateway-Secret", gatewaySecret)
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Name", claims.get("username", String.class))
                    .build();

            SecurityContext securityContext = new SecurityContextImpl();
            securityContext.setAuthentication(auth);
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();

            return chain.filter(newExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } catch (Exception e) {
            log.error("========== JWT validation failed", e);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorJson = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
