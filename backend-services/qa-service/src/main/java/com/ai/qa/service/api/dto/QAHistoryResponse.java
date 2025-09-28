package com.ai.qa.service.api.dto;

import java.time.LocalDateTime;

public class QAHistoryResponse {

    private final Long id;
    private final Long sessionId;
    private final Long userId;
    private final String question;
    private final String answer;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer latencyMs;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QAHistoryResponse(Long id,
                             Long sessionId,
                             Long userId,
                             String question,
                             String answer,
                             Integer promptTokens,
                             Integer completionTokens,
                             Integer latencyMs,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.latencyMs = latencyMs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}