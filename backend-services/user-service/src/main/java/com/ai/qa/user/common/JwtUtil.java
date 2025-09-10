package com.ai.qa.user.common;

import com.ai.qa.user.infrastructure.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成 JWT Token（仅包含用户名和过期时间）
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 签名算法和密钥
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 判断 Token 是否过期
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // 解析失败也视为过期
        }
    }

    /**
     * 解析 Token 获取所有 Claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token) // 解析并验证签名
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("无效的 JWT token: " + e.getMessage(), e);
        }
    }

    /**
     * 获取签名密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}