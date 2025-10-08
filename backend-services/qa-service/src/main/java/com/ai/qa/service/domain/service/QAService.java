package com.ai.qa.service.domain.service;

import com.ai.qa.service.api.dto.QAResponseDTO;
import com.ai.qa.service.domain.model.QARAG;
import com.ai.qa.service.infrastructure.feign.GeminiClient;
import com.ai.qa.service.infrastructure.feign.UserClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * QAService：处理用户问题并调用 Google Gemini AI 获取回答
 * 
 * 调用流程
 * QAController 接收到 /api/qa/ask 请求
 * 调用 QAService.processQuestion(userId, question)
 * QAService 先通过 UserClient 从 user-service 拿到用户信息
 * 再调用 GoogleAIClient，向 Google Gemini 发送问题，获取真实 AI 答案
 * 拼接并返回
 */
@Service
public class QAService {

    private final UserClient userClient;
    private final GeminiClient geminiClient;

    public QAService(UserClient userClient, GeminiClient geminiClient) {
        this.userClient = userClient;
        this.geminiClient = geminiClient;
    }

    /**
     * 处理用户问题并返回结构化结果
     * 
     * @param userId   用户ID
     * @param question 用户问题
     * @return QAResponseDTO 包含用户信息、问题、AI答案等
     */
    public QAResponseDTO processQuestion(Long userId, String question) {
        // 1. 获取用户信息
        String user;
        try {
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            return new QAResponseDTO(
                    userId,
                    "Unknown (error: " + e.getMessage() + ")",
                    question,
                    "Sorry, I cannot get your user information right now.",
                    LocalDateTime.now());
        }

        if (user == null) {
            return new QAResponseDTO(
                    userId,
                    "Not found",
                    question,
                    "Sorry, user with ID " + userId + " not found.",
                    LocalDateTime.now());
        }

        // 2. 调用 GeminiClient 获取 AI 答案
        String aiAnswer = geminiClient.askQuestion(userId, question);

        // 3. 封装 RAG 对象（可选，可扩展）
        QARAG rag = new QARAG("retrieved context", aiAnswer);

        // 4. 返回 JSON DTO
        return new QAResponseDTO(
                userId,
                user,
                question,
                rag.getGeneratedAnswer(),
                LocalDateTime.now());
    }
}
