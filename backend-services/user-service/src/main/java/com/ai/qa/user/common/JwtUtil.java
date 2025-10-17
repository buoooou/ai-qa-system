package com.ai.qa.user.common;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtUtil {

    /**
     * JWT密钥
     * 从配置文件中读取，用于签名和验证JWT
     * 密钥长度必须至少512位（64字节）以满足HS512算法要求
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 访问令牌有效期（毫秒）
     * 默认2小时
     */
    @Value("${jwt.access-expiration:7200000}")
    private long accessTokenExpiration;

    /**
     * 刷新令牌有效期（毫秒）
     * 默认7天
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 创建JWT令牌
     * 
     * @param claims 声明信息
     * @param subject 主题（通常是用户名）
     * @param expiration 过期时间（毫秒）
     * @return String JWT令牌
     */
    private String generateToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
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
        
        return generateToken(claims, username, refreshTokenExpiration);
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
        
        return generateToken(claims, username, accessTokenExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
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
            String tokenUsername = extractUsername(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("验证JWT令牌失败: {}", e.getMessage());
            return false;
        }
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
     * 从令牌中获取声明信息
     * 
     * @param token JWT令牌
     * @return Claims 声明信息
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
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
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("检查令牌过期状态失败: {}", e.getMessage());
            return true; // 出现异常时认为令牌已过期
        }
    }

    private Date extractExpiration(String token) {
        return parseToken(token).getExpiration();
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
