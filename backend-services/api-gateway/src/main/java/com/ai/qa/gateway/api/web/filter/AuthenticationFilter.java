package com.ai.qa.gateway.api.web.filter;

import com.ai.qa.gateway.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
        System.out.println("AuthenticationFilter triggered for path: " + request.getURI().getPath());
        String path = request.getURI().getPath();
        String authHeader = request.getHeaders().getFirst("Authorization");

        System.out.println("Incoming request path: " + path);
        System.out.println("Authorization header: " + authHeader);

        List<String> whiteList = List.of("/api/gateway/auth/login", "/api/gateway/auth/register");
        boolean isWhitelisted = whiteList.stream().anyMatch(path::startsWith);
        if (isWhitelisted) {
            System.out.println("Path " + path + " is whitelisted, skipping authentication");
            return chain.filter(exchange);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Missing or invalid Authorization header");
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

            System.out.println("Authenticated user: id=" + claims.getSubject()
                    + ", username=" + claims.get("username", String.class)
                    + ", role=" + claims.get("role", String.class));
            System.out.println("JWT claims: " + claims);
            System.out.println("Token subject: " + claims.getSubject());
            System.out.println("Token issuer: " + claims.getIssuer());
            System.out.println("Token expiration: " + claims.getExpiration());


            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("Authorization", authHeader)
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Name", claims.get("username", String.class))
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
