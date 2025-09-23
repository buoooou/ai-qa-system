package com.ai.qa.service.infrastructure.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 简化的 Google AI 服务
 * 用于测试 Google Gemini API 集成
 */
@Service
@Slf4j
public class SimpleGoogleAIService {

    @Value("${google.ai.api-key:AIzaSyBLjKio8iVAKZEPXkWa0wFP92VDjoScBtQ}")
    private String apiKey;

    private final WebClient webClient;

    public SimpleGoogleAIService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta")
            .build();
    }

    /**
     * 生成简单的 AI 回答（测试版本）
     */
    public CompletableFuture<String> generateSimpleAnswer(String question, String userId) {
        log.info("开始生成简单 AI 回答，用户ID: {}, 问题: {}", userId, question);
        
        // 先返回一个简单的测试回答
        String testAnswer = String.format(
            "您好！我是 AI 助手，很高兴为您服务。您的问题「%s」已经收到，用户ID为 %s。这是一个测试回答，Google AI 集成正在开发中。",
            question, userId
        );
        
        return CompletableFuture.completedFuture(testAnswer);
    }

    /**
     * 尝试调用真实的 Google Gemini API
     */
    public CompletableFuture<String> generateRealAnswer(String question, String userId) {
        log.info("开始调用真实的 Google Gemini API，用户ID: {}, 问题: {}", userId, question);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            
            String prompt = String.format(
                "你是一个智能助手，用户ID为 %s。请回答以下问题：\n\n问题：%s\n\n请用中文回答，回答要准确、有帮助且简洁。",
                userId, question
            );
            
            part.put("text", prompt);
            contents.put("parts", List.of(part));
            requestBody.put("contents", List.of(contents));

            // 调用 Google Gemini API
            String url = String.format("/models/gemini-1.5-flash:generateContent?key=%s", apiKey);
            
            return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("Google API 响应: {}", response);
                    // 简单解析响应
                    if (response.contains("\"text\"")) {
                        int start = response.indexOf("\"text\":\"") + 8;
                        int end = response.indexOf("\"", start);
                        if (start > 7 && end > start) {
                            return response.substring(start, end).replace("\\n", "\n");
                        }
                    }
                    return "抱歉，我无法解析 AI 回答。原始响应：" + response;
                })
                .doOnSuccess(answer -> log.info("AI 回答生成成功，用户ID: {}, 回答: {}", userId, answer))
                .doOnError(error -> log.error("AI 回答生成失败", error))
                .toFuture();

        } catch (Exception e) {
            log.error("调用 Google AI 服务失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
