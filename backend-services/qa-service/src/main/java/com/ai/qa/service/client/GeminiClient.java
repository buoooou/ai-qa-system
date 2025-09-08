package com.ai.qa.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini AI客户端
 * 
 * 负责与Google Gemini API进行通信，发送问题并获取AI回答
 * 封装了HTTP请求的细节，提供简单易用的接口
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Slf4j          // Lombok注解：自动生成日志对象
@Component      // Spring注解：标识这是一个组件，由Spring管理
public class GeminiClient {
    
    /**
     * Gemini API的基础URL
     * 从配置文件中读取，便于环境切换
     */
    @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;
    
    /**
     * Gemini API Key
     * 从配置文件中读取，需要在Google AI Studio获取
     * 如果未配置，将使用默认提示信息
     */
    @Value("${gemini.api.key:YOUR_API_KEY_HERE}")
    private String apiKey;
    
    /**
     * 使用的模型名称
     * 默认使用gemini-pro模型
     */
    @Value("${gemini.api.model:gemini-pro}")
    private String model;
    
    /**
     * HTTP客户端
     * 用于发送HTTP请求
     */
    private final RestTemplate restTemplate;
    
    /**
     * 构造函数
     * 初始化RestTemplate
     */
    public GeminiClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 向Gemini AI发送问题并获取回答
     * 
     * @param question 用户问题
     * @return String AI的回答
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    public String askQuestion(String question) {
        log.info("开始调用Gemini API，问题长度: {}", question.length());
        
        // 检查API Key是否已配置
        if ("YOUR_API_KEY_HERE".equals(apiKey)) {
            log.warn("Gemini API Key未配置，返回模拟回答");
            return generateMockResponse(question);
        }
        
        try {
            // 构建请求URL
            String url = String.format("%s/models/%s:generateContent?key=%s", 
                                     baseUrl, model, apiKey);
            
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(question);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建HTTP请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送POST请求
            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, Map.class);
            long endTime = System.currentTimeMillis();
            
            log.info("Gemini API调用成功，耗时: {}ms", endTime - startTime);
            
            // 解析响应
            return parseResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("调用Gemini API失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI服务暂时不可用，请稍后重试");
        }
    }
    
    /**
     * 构建Gemini API请求体
     * 
     * @param question 用户问题
     * @return Map<String, Object> 请求体
     */
    private Map<String, Object> buildRequestBody(String question) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // 构建contents数组
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", question);
        content.put("parts", List.of(part));
        
        requestBody.put("contents", List.of(content));
        
        // 设置生成配置（可选）
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);  // 控制回答的创造性
        generationConfig.put("maxOutputTokens", 1000);  // 限制回答长度
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }
    
    /**
     * 解析Gemini API响应
     * 
     * @param responseBody API响应体
     * @return String 提取的AI回答
     */
    @SuppressWarnings("unchecked")
    private String parseResponse(Map<String, Object> responseBody) {
        try {
            // 解析响应结构：candidates[0].content.parts[0].text
            List<Map<String, Object>> candidates = 
                (List<Map<String, Object>>) responseBody.get("candidates");
            
            if (candidates == null || candidates.isEmpty()) {
                log.warn("Gemini API响应中没有candidates");
                return "抱歉，我现在无法回答这个问题。";
            }
            
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = 
                (Map<String, Object>) firstCandidate.get("content");
            
            if (content == null) {
                log.warn("Gemini API响应中没有content");
                return "抱歉，我现在无法回答这个问题。";
            }
            
            List<Map<String, Object>> parts = 
                (List<Map<String, Object>>) content.get("parts");
            
            if (parts == null || parts.isEmpty()) {
                log.warn("Gemini API响应中没有parts");
                return "抱歉，我现在无法回答这个问题。";
            }
            
            String text = (String) parts.get(0).get("text");
            return text != null ? text.trim() : "抱歉，我现在无法回答这个问题。";
            
        } catch (Exception e) {
            log.error("解析Gemini API响应失败: {}", e.getMessage(), e);
            return "抱歉，处理回答时出现了问题。";
        }
    }
    
    /**
     * 生成模拟回答（当API Key未配置时使用）
     * 
     * @param question 用户问题
     * @return String 模拟的AI回答
     */
    private String generateMockResponse(String question) {
        // 简单的关键词匹配，生成相应的模拟回答
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("你好") || lowerQuestion.contains("hello")) {
            return "你好！我是AI智能助手，很高兴为您服务。请问有什么可以帮助您的吗？";
        } else if (lowerQuestion.contains("天气")) {
            return "抱歉，我目前无法获取实时天气信息。建议您查看天气预报应用或网站获取准确的天气信息。";
        } else if (lowerQuestion.contains("时间")) {
            return "我无法获取当前时间，请查看您的设备时钟。如果您需要其他帮助，请告诉我！";
        } else {
            return String.format("感谢您的问题：\"%s\"\n\n" +
                               "这是一个模拟回答，因为Gemini API Key尚未配置。\n" +
                               "要获得真实的AI回答，请：\n" +
                               "1. 访问 Google AI Studio (https://makersuite.google.com/)\n" +
                               "2. 获取您的API Key\n" +
                               "3. 在配置文件中设置 gemini.api.key 属性", 
                               question);
        }
    }
}
