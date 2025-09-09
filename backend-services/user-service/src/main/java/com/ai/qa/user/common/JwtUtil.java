package com.ai.qa.user.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 负责生成、解析和验证 JWT 令牌，使用 HS512 算法
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Component
public class JwtUtil {

    /**
     * JWT 签名密钥（Base64 编码）
     * 建议长度至少 256 位，配置于 application.yml
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT 有效期（毫秒）
     * 例如：3600000 = 1 小时
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * payload 中用户名键名
     */
    private static final String CLAIM_KEY_USERNAME = "username";

    /**
     * 获取 HS512 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT 令牌
     *
     * @param username 用户名
     * @return 令牌字符串
     */
    public String generateToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 从令牌中提取用户名
     *
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        JwtParser parser = Jwts.parser()
                .verifyWith(getSigningKey())
                .build();

        Jws<Claims> jws = parser.parseSignedClaims(token);
        return jws.getPayload().get(CLAIM_KEY_USERNAME, String.class);
    }

    /**
     * 验证令牌有效性
     *
     * @param token JWT 令牌
     * @return 有效返回 true，否则返回 false
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
            parser.parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                SignatureException | IllegalArgumentException e) {
            // Handle specific exceptions (excluding JwtException, as it's a parent class)
            return false;
        } catch (JwtException e) {
            // Catch any remaining JwtException (parent class) not handled above
            return false;
        }
    }

    /**
     * 获取令牌剩余有效期（毫秒）
     *
     * @param token JWT 令牌
     * @return 剩余时间，负数表示已过期
     */
    public long getRemainingExpiration(String token) {
        JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
        Jws<Claims> jws = parser.parseSignedClaims(token);
        Date expiration = jws.getPayload().getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}