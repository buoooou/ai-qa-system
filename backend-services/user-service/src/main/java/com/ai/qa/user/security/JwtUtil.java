package com.ai.qa.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:ai-qa-system-secret-key-for-jwt-token-generation}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24小时，单位毫秒
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

    /**
     * 验证Token是否有效
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查Token是否过期
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}