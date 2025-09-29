package com.ai.qa.user.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-minutes}")
    private long expirationMinutes;

    private Key key;

    @PostConstruct
    void initKey() {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes) long");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(Long userId, String username, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(expirationMinutes));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("username", username)
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getExpirationSeconds() {
        return Duration.ofMinutes(expirationMinutes).toSeconds();
    }

    public Key getKey() {
        return this.key;
    }

}
