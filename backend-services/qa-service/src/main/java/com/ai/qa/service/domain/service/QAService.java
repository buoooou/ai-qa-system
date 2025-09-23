package com.ai.qa.service.domain.service;

import com.ai.qa.service.infrastructure.ai.DeepSeekAIService;
import com.ai.qa.service.infrastructure.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class QAService {

    private final UserClient userClient;
    private final DeepSeekAIService deepSeekAIService;

    /**
     * 处理用户问题（测试用，返回固定消息）
     */
    public String processQuestion(Long userId) {
        log.info("处理用户问题，用户ID: {}", userId);
        return "您好！我是 AI 助手，很高兴为您服务。请问有什么可以帮助您的吗？";
    }

    /**
     * 使用 Google AI 处理用户问题
     * @param question 用户问题
     * @param userId 用户ID
     * @return AI 回答
     */
    public CompletableFuture<String> processQuestionWithAI(String question, String userId) {
        log.info("使用 Google AI 处理问题，用户ID: {}, 问题: {}", userId, question);
        
        // 获取用户信息（可选）
        String userInfo = null;
        try {
            userInfo = userClient.getUserById(Long.parseLong(userId));
            log.info("获取到用户信息: {}", userInfo);
        } catch (Exception e) {
            log.warn("无法获取用户信息: {}", e.getMessage());
            // 继续处理，不因为用户信息获取失败而中断
        }

        // 使用 DeepSeek AI 服务
        return deepSeekAIService.generateAnswer(question, userId);
    }
}
