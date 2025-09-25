package com.ai.qa.gateway.util;

import com.ai.qa.gateway.service.TokenStorageService;
import com.ai.qa.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Autowired
    private TokenStorageService tokenStorageService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    // Token过期时间（毫秒），这里设置为24小时
    private static final long EXPIRATION_TIME = 86400000;
    
    // Token续期阈值（毫秒），当剩余时间少于30分钟时续期
    private static final long RENEWAL_THRESHOLD = 1800000;
    
    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(String userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
        
        // 存储Token到本地内存
        tokenStorageService.storeToken(token, expiryDate);
        
        return token;
    }
    
    /**
     * 验证JWT Token
     * @param token JWT Token
     * @return Claims对象，包含Token中的信息
     */
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从Token中提取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", String.class);
    }
    
    /**
     * 从Token中提取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 检查Token是否过期
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
    
    /**
     * 检查Token是否在本地存储中存在且有效
     * @param token JWT Token
     * @return Token是否有效
     */
    public boolean isTokenValidInStorage(String token) {
        return tokenStorageService.isValidToken(token);
    }
    
    /**
     * 检查Token是否需要续期
     * @param token JWT Token
     * @return 是否需要续期
     */
    public boolean shouldRenewToken(String token) {
        if (!isTokenValidInStorage(token)) {
            return false;
        }
        
        Claims claims = validateToken(token);
        Date expiration = claims.getExpiration();
        Date now = new Date();
        
        // 计算剩余时间
        long timeRemaining = expiration.getTime() - now.getTime();
        
        // 如果剩余时间小于续期阈值，则需要续期
        return timeRemaining < RENEWAL_THRESHOLD;
    }
    
    /**
     * 续期Token
     * @param token JWT Token
     * @return 新的Token或原Token
     */
    public String renewTokenIfNeeded(String token) {
        // 检查Token是否有效
        if (!isTokenValidInStorage(token)) {
            return null;
        }
        
        // 检查是否需要续期
        if (shouldRenewToken(token)) {
            // 提取用户信息
            String userId = getUserIdFromToken(token);
            String username = getUsernameFromToken(token);
            
            // 生成新Token
            String newToken = generateToken(userId, username);
            
            // 从存储中移除旧Token
            tokenStorageService.removeToken(token);
            
            return newToken;
        }
        
        // 不需要续期，返回原Token
        return token;
    }
    
    /**
     * 获取签名密钥
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }
}