package com.ai.qa.gateway.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;

@Service
public class TokenStorageService {
    
    // 使用ConcurrentHashMap存储Token信息，key为Token，value为过期时间
    private final Map<String, Date> tokenStore = new ConcurrentHashMap<>();
    
    /**
     * 存储Token及其过期时间
     * @param token JWT Token
     * @param expiration 过期时间
     */
    public void storeToken(String token, Date expiration) {
        tokenStore.put(token, expiration);
    }
    
    /**
     * 检查Token是否存在且未过期
     * @param token JWT Token
     * @return Token是否有效
     */
    public boolean isValidToken(String token) {
        Date expiration = tokenStore.get(token);
        if (expiration == null) {
            return false;
        }
        
        // 检查是否过期
        return expiration.after(new Date());
    }
    
    /**
     * 删除Token
     * @param token JWT Token
     */
    public void removeToken(String token) {
        tokenStore.remove(token);
    }
    
    /**
     * 续期Token
     * @param token JWT Token
     * @param newExpiration 新的过期时间
     */
    public void renewToken(String token, Date newExpiration) {
        if (tokenStore.containsKey(token)) {
            tokenStore.put(token, newExpiration);
        }
    }
    
    /**
     * 清理过期的Token
     */
    public void cleanExpiredTokens() {
        Date now = new Date();
        tokenStore.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}