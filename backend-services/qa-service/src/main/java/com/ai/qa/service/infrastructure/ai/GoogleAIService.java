package com.ai.qa.service.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Google AI 服务
 * 使用 Google Gemini API 生成回答
 */
@Service
@Slf4j
public class GoogleAIService {

    @Value("${google.ai.api-key:AIzaSyBLjKio8iVAKZEPXkWa0wFP92VDjoScBtQ}")
    private String apiKey;

    @Value("${google.ai.model:gemini-2.0-flash-exp}")
    private String modelName;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GoogleAIService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 生成 AI 回答
     * @param question 用户问题
     * @param userId 用户ID
     * @return AI 回答
     */
    public CompletableFuture<String> generateAnswer(String question, String userId) {
        log.info("开始生成 AI 回答，用户ID: {}, 问题: {}", userId, question);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            
            // 构建提示词
            String prompt = buildPrompt(question, userId);
            part.put("text", prompt);
            contents.put("parts", List.of(part));
            requestBody.put("contents", List.of(contents));

            // 调用 Google Gemini API
            String url = String.format("/models/%s:generateContent?key=%s", modelName, apiKey);
            
            log.info("调用 Google Gemini API，URL: {}, 请求体: {}", url, objectMapper.writeValueAsString(requestBody));
            
            return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("Google API 原始响应: {}", response);
                    return parseResponse(response);
                })
                .doOnSuccess(answer -> log.info("AI 回答生成成功，用户ID: {}, 回答: {}", userId, answer))
                .doOnError(error -> log.error("AI 回答生成失败，用户ID: {}, 错误: {}", userId, error.getMessage()))
                .toFuture();

        } catch (Exception e) {
            log.error("初始化 Google AI 服务失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 解析 API 响应
     */
    private String parseResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode candidates = jsonNode.get("candidates");
            
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.get("content");
                
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode firstPart = parts.get(0);
                        JsonNode text = firstPart.get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }
            
            // 如果没有找到答案，返回默认消息
            return "抱歉，我无法生成回答。请稍后再试。";
            
        } catch (Exception e) {
            log.error("解析 Google AI 响应失败", e);
            return "抱歉，处理回答时出现错误。";
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(String question, String userId) {
        return String.format(
            "你是一个智能助手，用户ID为 %s。请回答以下问题：\n\n" +
            "问题：%s\n\n" +
            "请用中文回答，回答要准确、有帮助且简洁。",
            userId,
            question
        );
    }
}
