package com.ai.qa.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * AI模型客户端，用于调用外部AI服务
 */
@Component
public class AIModelClient {

    private final RestTemplate restTemplate;

    @Value("${chat.api.url}")
    private String chatApiUrl;

    @Value("${chat.api.key}")
    private String apiKey;

    public AIModelClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 调用Google Gemini AI获取回答
     * @param question 用户问题
     * @return AI生成的回答
     */
    public String getAIResponse(String question) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();

        // 构建Google Gemini API请求格式
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", question);
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // 在URL中添加API密钥参数
            String urlWithKey = chatApiUrl + "?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithKey, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("candidates")) {
                java.util.List candidates = (java.util.List) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = (Map<String, Object>) candidates.get(0);
                    Map<String, Object> content_response = (Map<String, Object>) firstCandidate.get("content");
                    java.util.List parts = (java.util.List) content_response.get("parts");
                    if (!parts.isEmpty()) {
                        Map<String, Object> firstPart = (Map<String, Object>) parts.get(0);
                        return (String) firstPart.get("text");
                    }
                }
            }
            return "抱歉，AI服务暂时无法回答您的问题。";
        } catch (Exception e) {
            System.err.println("Google Gemini API调用异常: " + e.getMessage());
            return "抱歉，AI服务出现异常，请稍后重试。";
        }
    }
}
