package com.ai.qa.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "问答请求DTO")
public class QuestionRequest {

    @Schema(description = "问题内容", required = true, example = "什么是人工智能？")
    private String question;

    @Schema(description = "用户ID", required = true, example = "1")
    private Long userId;

    @Schema(description = "会话ID", example = "session_123")
    private String sessionId;

    @Schema(description = "问题类型", example = "general")
    private String questionType;

    // Getters and Setters (fallback for Lombok issues)
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
}
