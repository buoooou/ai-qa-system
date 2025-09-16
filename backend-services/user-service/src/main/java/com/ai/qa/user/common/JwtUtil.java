package com.ai.qa.user.common;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * 提供JWT令牌的生成、解析、验证功能
 * 支持访问令牌和刷新令牌的管理
 *
 * @author David
 * @version 1.0
 * @since 2025-09-08
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     * 从配置文件中读取，用于签名和验证JWT
     * 密钥长度必须至少512位（64字节）以满足HS512算法要求
     */
    @Value("${jwt.secret:ai-qa-system-secret-key-for-jwt-token-generation-and-validation-must-be-at-least-512-bits-long-for-hs512-algorithm}")
    private String secret;

    /**
     * 访问令牌有效期（毫秒）
     * 默认2小时
     */
    @Value("${jwt.access-token-expiration:7200000}")
    private Long accessTokenExpiration;

    /**
     * 刷新令牌有效期（毫秒）
     * 默认7天
     */
    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    /**
     * 获取签名密钥
     *
     * @return SecretKey 签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return String JWT访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "access");

        return createToken(claims, username, accessTokenExpiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return String JWT刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");

        return createToken(claims, username, refreshTokenExpiration);
    }

    /**
     * 创建JWT令牌
     *
     * @param claims 声明信息
     * @param subject 主题（通常是用户名）
     * @param expiration 过期时间（毫秒）
     * @return String JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return String 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return Long 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取令牌类型
     *
     * @param token JWT令牌
     * @return String 令牌类型（access/refresh）
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("tokenType", String.class);
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT令牌
     * @return Date 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 从令牌中获取声明信息
     *
     * @param token JWT令牌
     * @return Claims 声明信息
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("解析JWT令牌失败: {}", e.getMessage());
            throw new RuntimeException("无效的JWT令牌", e);
        }
    }

    /**
     * 验证令牌是否过期
     *
     * @param token JWT令牌
     * @return boolean true-已过期，false-未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("检查令牌过期状态失败: {}", e.getMessage());
            return true; // 出现异常时认为令牌已过期
        }
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌
     * @param username 用户名
     * @return boolean true-有效，false-无效
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("验证JWT令牌失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证令牌是否为访问令牌
     *
     * @param token JWT令牌
     * @return boolean true-是访问令牌，false-不是访问令牌
     */
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType);
        } catch (Exception e) {
            log.error("检查令牌类型失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证令牌是否为刷新令牌
     *
     * @param token JWT令牌
     * @return boolean true-是刷新令牌，false-不是刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            log.error("检查令牌类型失败: {}", e.getMessage());
            return false;
        }
    }
}