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

    @Value("${ai.model.api.url}")
    private String aiModelApiUrl;

    @Value("${ai.model.api.key}")
    private String apiKey;

    public AIModelClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 调用AI模型获取回答
     * @param question 用户问题
     * @return AI生成的回答
     */
    public String getAIResponse(String question) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            Map.of("role", "user", "content", question)
        });
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(aiModelApiUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                Object[] choices = (Object[]) responseBody.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> firstChoice = (Map<String, Object>) choices[0];
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
            return "抱歉，AI服务暂时无法回答您的问题。";
        } catch (Exception e) {
            return "抱歉，AI服务出现异常，请稍后重试。";
        }
    }
}
