package com.ai.qa.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication.getCredentials().toString())
                .flatMap(token -> {
                    try {
                        // 解析JWT令牌
                        Claims claims = Jwts.parser()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseSignedClaims(token)
                                .getBody();

                        String username = claims.getSubject();

                        // 移除角色权限（无需角色鉴权）
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                username,
                                token,
                                Collections.emptyList() // 空权限列表
                        );

                        return Mono.just(auth);
                    } catch (ExpiredJwtException e) {
                        return Mono.error(new RuntimeException("JWT令牌已过期"));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("无效的JWT令牌: " + e.getMessage()));
                    }
                });
    }
}