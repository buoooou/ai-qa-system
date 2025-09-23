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
 * DeepSeek AI 服务
 * 使用 DeepSeek API 生成回答
 */
@Service
@Slf4j
public class DeepSeekAIService {

    @Value("${deepseek.ai.api-key:sk-807a0b59ed5f45d9812567e82db4e6c1}")
    private String apiKey;

    @Value("${deepseek.ai.model:deepseek-chat}")
    private String modelName;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public DeepSeekAIService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.deepseek.com")
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
        log.info("开始生成 DeepSeek AI 回答，用户ID: {}, 问题: {}", userId, question);
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", buildPrompt(question, userId))
            ));
            requestBody.put("stream", false);

            // 调用 DeepSeek API
            String url = "/v1/chat/completions";
            
            log.info("调用 DeepSeek API，URL: {}, 请求体: {}", url, objectMapper.writeValueAsString(requestBody));
            
            return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("DeepSeek API 原始响应: {}", response);
                    return parseResponse(response);
                })
                .doOnSuccess(answer -> log.info("AI 回答生成成功，用户ID: {}, 回答: {}", userId, answer))
                .doOnError(error -> log.error("AI 回答生成失败，用户ID: {}, 错误: {}", userId, error.getMessage()))
                .toFuture();

        } catch (Exception e) {
            log.error("初始化 DeepSeek AI 服务失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 解析 API 响应
     */
    private String parseResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode choices = jsonNode.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            
            // 如果没有找到答案，返回默认消息
            return "抱歉，我无法生成回答。请稍后再试。";
            
        } catch (Exception e) {
            log.error("解析 DeepSeek AI 响应失败", e);
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
