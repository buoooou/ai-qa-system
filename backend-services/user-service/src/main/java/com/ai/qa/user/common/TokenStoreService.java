package com.ai.qa.user.common;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 令牌存储服务，用于管理用户的有效令牌和黑名单
 * 注意：这是一个简化实现，实际项目中应考虑使用分布式缓存如Redis
 */
@Service
public class TokenStoreService {

    // 存储有效令牌，key为用户名，value为令牌列表
    private final Map<String, List<String>> validTokens = new ConcurrentHashMap<>();

    // 令牌黑名单，存储已失效的令牌
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    // 清理黑名单的时间间隔（毫秒）
    private static final long BLACKLIST_CLEAN_INTERVAL = 24 * 60 * 60 * 1000; // 24小时

    public TokenStoreService() {
        // 启动定期清理黑名单的任务
        startBlacklistCleanupTask();
    }

    /**
     * 存储用户的有效令牌
     * @param username 用户名
     * @param token 令牌
     */
    public void storeToken(String username, String token) {
        validTokens.computeIfAbsent(username, k -> new ArrayList<>()).add(token);
    }

    /**
     * 从存储中移除用户的令牌
     * @param username 用户名
     * @param token 令牌
     */
    public void removeToken(String username, String token) {
        List<String> tokens = validTokens.get(username);
        if (tokens != null) {
            tokens.remove(token);
            if (tokens.isEmpty()) {
                validTokens.remove(username);
            }
        }
    }

    /**
     * 将令牌添加到黑名单
     * @param token 令牌
     */
    public void addToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    /**
     * 检查令牌是否在黑名单中
     * @param token 令牌
     * @return 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    /**
     * 获取用户的所有有效令牌
     * @param username 用户名
     * @return 令牌列表
     */
    public List<String> getUserTokens(String username) {
        return validTokens.getOrDefault(username, Collections.emptyList());
    }

    /**
     * 清理用户的所有过期令牌
     * @param username 用户名
     * @param jwtUtil JWT工具类
     */
    public void cleanupExpiredTokens(String username, JwtUtil jwtUtil) {
        List<String> tokens = validTokens.get(username);
        if (tokens != null) {
            // 过滤出有效的令牌
            List<String> validTokensList = tokens.stream()
                    .filter(token -> !isTokenBlacklisted(token) && !jwtUtil.isTokenExpired(token))
                    .collect(Collectors.toList());
            
            if (validTokensList.isEmpty()) {
                validTokens.remove(username);
            } else {
                validTokens.put(username, validTokensList);
            }
        }
    }

    /**
     * 启动定期清理黑名单的任务
     */
    private void startBlacklistCleanupTask() {
        Timer timer = new Timer(true); // 使用守护线程
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 实际项目中应该根据令牌的过期时间进行清理
                System.out.println("清理黑名单任务执行");
                // 这里简化处理，不做具体清理逻辑
            }
        }, BLACKLIST_CLEAN_INTERVAL, BLACKLIST_CLEAN_INTERVAL);
    }

    public Boolean isTokenValid(String token) {
        return null;
    }
}