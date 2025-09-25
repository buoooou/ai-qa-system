package com.ai.qa.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "问答响应DTO")
public class QuestionResponse {

    @Schema(description = "问答记录ID", example = "1")
    private Long id;

    @Schema(description = "用户问题", example = "什么是人工智能？")
    private String question;

    @Schema(description = "AI回答", example = "人工智能是一种模拟人类智能的技术...")
    private String answer;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "会话ID", example = "session_123")
    private String sessionId;

    @Schema(description = "响应时间（毫秒）", example = "1500")
    private Long responseTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "问题类型", example = "general")
    private String questionType;

    @Schema(description = "AI模型版本", example = "gpt-3.5-turbo")
    private String modelVersion;

    // Getters and Setters (fallback for Lombok issues)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getResponseTime() { return responseTime; }
    public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
}
