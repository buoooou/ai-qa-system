package com.ai.qa.service.application.service;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Gemini2Service {
    private final Client client;

    public String generate(String prompt) {
        try{
            GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-pro",
                prompt,
                null);
            return response.text();
            // return generateMockResponse(prompt);
        } catch(Exception ex) {
            return generateMockResponse(prompt);
        }

    }

    private String generateMockResponse(String question) {
        // 简单的关键词匹配，生成相应的模拟回答
        String lowerQuestion = question.toLowerCase();
        if (lowerQuestion.contains("你好") || lowerQuestion.contains("hello") || lowerQuestion.contains("介绍")) {
            return "你好！我是AI智能客服助手，很高兴为您服务！\n\n" +
                   "我可以帮助您：\n" +
                   "• 回答各种问题\n" +
                   "• 提供信息查询\n" +
                   "• 协助解决问题\n" +
                   "• 进行日常对话\n\n" +
                   "请随时告诉我您需要什么帮助！";
        } else if (lowerQuestion.contains("天气")) {
            return "关于天气查询：\n\n" +
                   "抱歉，我目前无法获取实时天气信息。建议您：\n" +
                   "• 查看手机天气应用\n" +
                   "• 访问天气预报网站\n" +
                   "• 询问语音助手\n\n" +
                   "如果您有其他问题，我很乐意帮助您！";
        } else if (lowerQuestion.contains("时间")) {
            return "关于时间查询：\n\n" +
                   "我无法获取当前准确时间，请查看您的设备时钟。\n\n" +
                   "如果您需要其他帮助，比如时间管理建议或日程安排，我很乐意协助您！";
        } else if (lowerQuestion.contains("帮助") || lowerQuestion.contains("功能")) {
            return "我是您的AI智能助手，可以为您提供以下服务：\n\n" +
                   ":memo: 信息查询和解答\n" +
                   ":bulb: 问题分析和建议\n" +
                   ":speaking_head_in_silhouette: 日常对话交流\n" +
                   ":books: 知识分享\n" +
                   ":handshake: 生活和工作建议\n\n" +
                   "请告诉我您想了解什么，我会尽力帮助您！";
        } else if (lowerQuestion.contains("谢谢") || lowerQuestion.contains("感谢")) {
            return "不客气！很高兴能够帮助您。\n\n" +
                   "如果您还有其他问题或需要进一步的帮助，请随时告诉我。我会一直在这里为您服务！:blush:";
        } else {
            return String.format("感谢您的提问：\"%s\"\n\n" +
                               "我正在努力理解您的问题。作为AI助手，我会尽力为您提供有用的信息和建议。\n\n" +
                               "如果您能提供更多详细信息，我将能够给出更准确的回答。\n\n" +
                               "请问您还有什么其他问题需要我帮助解决吗？",
                               question);
        }
    }
}
