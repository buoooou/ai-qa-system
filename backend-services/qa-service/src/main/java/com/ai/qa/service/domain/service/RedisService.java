package com.ai.qa.service.domain.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 存储会话 ID 和用户 ID 的映射
    public void saveSession(String sessionId, String userId) {
        redisTemplate.opsForValue().set("session:" + sessionId, userId);
        // 设置过期时间（例如 24 小时）
        redisTemplate.expire("session:" + sessionId, 24, TimeUnit.HOURS);
    }

    // 获取会话对应的用户 ID
    public String getUserIdBySession(String sessionId) {
        return redisTemplate.opsForValue().get("session:" + sessionId);
    }

    // 删除会话
    public boolean deleteSession(String sessionId) {
        return redisTemplate.delete("session:" + sessionId);
    }
}
