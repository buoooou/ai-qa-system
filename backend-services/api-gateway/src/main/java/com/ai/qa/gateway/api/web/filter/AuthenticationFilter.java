package com.ai.qa.gateway.api.web.filter;

import com.ai.qa.gateway.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Gateway filter performing JWT authentication for downstream services.
 */
@Slf4j
@Component
@RefreshScope
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;

    public AuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.debug("AuthenticationFilter triggered for path: {}", request.getURI().getPath());
        String path = request.getURI().getPath();
        String authHeader = request.getHeaders().getFirst("Authorization");

        log.debug("Incoming request path: {}", path);
        log.debug("Authorization header: {}", authHeader);

        List<String> whiteList = List.of("/api/gateway/auth/login", "/api/gateway/auth/register");
        boolean isWhitelisted = whiteList.stream().anyMatch(path::startsWith);
        if (isWhitelisted) {
            log.info("Path {} is whitelisted, skipping authentication", path);
            return chain.filter(exchange);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Authenticated user: id={}, username={}, role={}",
                    claims.getSubject(),
                    claims.get("username", String.class),
                    claims.get("role", String.class));

            log.debug("JWT claims: {}", claims);
            log.debug("Token subject: {}", claims.getSubject());
            log.debug("Token issuer: {}", claims.getIssuer());
            log.debug("Token expiration: {}", claims.getExpiration());

            ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(httpHeaders -> {
                        httpHeaders.set("Authorization", authHeader); // 覆盖原有值
                        httpHeaders.set("X-User-Id", claims.getSubject());
                        httpHeaders.set("X-User-Name", claims.get("username", String.class));
                        httpHeaders.set("X-User-Role", claims.get("role", String.class));
                    })
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("JWT validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}