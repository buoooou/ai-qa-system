package com.ai.qa.service.infrastructure.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ai.qa.service.infrastructure.config.GoogleAIProperties;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Gemini AI 客户端，用于调用 Google Gemini API
 * 
 * Nacos 配置：
 * google:
 * ai:
 * api-key: AIzaSyCGvYQa-5kJDbbXsr372wHsHBWEDtufjBM
 * model: gemini-1.5-flash
 * base-url: https://generativelanguage.googleapis.com/v1beta
 */

@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
public class GeminiClient {

    private final GoogleAIProperties googleAIProperties;
    private final RestTemplate restTemplate;

    // 用户 -> 历史对话上下文
    private static final Map<Long, List<Map<String, Object>>> conversationHistory = new HashMap<>();

    /**
     * 调用 Gemini API（自动检测中文）
     */
    public String askQuestion(Long userId, String question) {
        String apiKey = googleAIProperties.getApiKey();
        String model = "gemini-2.5-flash";
        String baseUrl = "https://generativelanguage.googleapis.com/v1beta";

        // （临时硬编码，确保稳定）
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "AIzaSyCGvYQa-5kJDbbXsr372wHsHBWEDtufjBM";
        }

        try {
            // 获取当前用户历史对话
            List<Map<String, Object>> history = conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>());

            // 添加用户消息
            history.add(Map.of("role", "user", "parts", List.of(Map.of("text", question))));

            // 构建请求体
            Map<String, Object> body = new HashMap<>();
            body.put("contents", history);
            body.put("generationConfig", Map.of(
                    "temperature", 0.8,
                    "maxOutputTokens", 2048));

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String url = String.format("%s/models/%s:generateContent?key=%s", baseUrl, model, apiKey);

            long start = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            long duration = System.currentTimeMillis() - start;
            log.info("✅ Gemini 响应成功，耗时 {} ms", duration);

            // 解析 Gemini 响应
            String answer = parseResponse(response.getBody());

            // 若原提问为中文，则尝试翻译回答为中文
            // if (isChinese) {
            // try {
            // String chinese = translationService.toChinese(answer);
            // if (chinese != null && !chinese.isBlank()) {
            // answer = chinese;
            // }
            // } catch (Exception e) {
            // log.warn("翻译回答失败，保留英文原文");
            // }
            // }

            // 保存 AI 回复到上下文
            history.add(Map.of("role", "model", "parts", List.of(Map.of("text", answer))));

            // 只保留最近 20 条
            if (history.size() > 20) {
                conversationHistory.put(userId,
                        new ArrayList<>(history.subList(history.size() - 20, history.size())));
            }

            return answer;

        } catch (Exception e) {
            log.error("❌ 调用 Gemini API 失败: {}", e.getMessage(), e);
            return "抱歉，AI 服务暂时不可用。";
        }
    }

    @SuppressWarnings("unchecked")
    private String parseResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty())
                return "抱歉，我没有理解你的问题。";

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null)
                return "抱歉，我没有理解你的问题。";

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty())
                return "抱歉，我没有理解你的问题。";

            return Objects.toString(parts.get(0).get("text"), "").trim();
        } catch (Exception e) {
            log.error("解析 Gemini 响应出错: {}", e.getMessage());
            return "抱歉，AI 回复解析失败。";
        }
    }

    private String generateMockResponse(String question) {
        return "这是模拟回答: " + question;
    }
}
