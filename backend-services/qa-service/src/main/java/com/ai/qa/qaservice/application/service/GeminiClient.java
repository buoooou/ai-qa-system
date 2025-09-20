package com.ai.qa.qaservice.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Value("${google.ai.endpoint}")
    private String endpoint;

    @Value("${google.ai.model:gemini-1.5-flash}")
    private String model;

    /**
     * 调用Gemini API获取回答
     * 
     * @param prompt 提示词
     * @return AI生成的回答
     */
    public String getGeminiResponse(String prompt) {
        log.info("====== 调用Gemini API获取回答: prompt = {} ====== ", prompt);
        log.info("apiKey: {}, endpoint: {}, model: {}", apiKey, endpoint, model);

        try {
            // 创建HTTP头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();

            // 构建内容列表
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            Map<String, String> parts = new HashMap<>();
            parts.put("text", prompt);
            userMessage.put("role", "user");
            userMessage.put("parts", List.of(parts));
            contents.add(userMessage);

            requestBody.put("contents", contents);

            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            String apiUrl = String.format("%s/models/%s:generateContent?key=%s", endpoint, model, apiKey);

            // 打印调用参数
            log.info("请求Gemini API: url = {}", apiUrl);
            log.info("请求Gemini API: requestEntity.requestBody = {}", requestBody.toString());
            log.info("请求Gemini API: requestEntity.headers = {}", headers.toString());

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity, String.class);

            // 打印响应内容
            // log.info(" response = {}", response);

            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode textNode = root.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text");

                if (textNode.isTextual()) {
                    return textNode.asText();
                }
            }

            return "获取Gemini响应失败: " + response.getStatusCode();
        } catch (Exception e) {
            throw new RuntimeException("调用Gemini API时发生错误", e);
        }
    }
}
