package com.ai.qa.qaservice.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.ai.qa.qaservice.application.model.Message;

@Data
@Builder
public class QuestionResponse {
    private String conversationId; // 对话ID
    private String question; // 原始提问
    private String answer; // AI回答
    private LocalDateTime timestamp; // 时间戳
    private List<Message> conversationHistory; // 对话历史（可选）
}
