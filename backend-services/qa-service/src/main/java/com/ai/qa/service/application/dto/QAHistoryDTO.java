package com.ai.qa.service.application.dto;

import java.time.LocalDateTime;

public class QAHistoryDTO {
    private final Long id;
    private final String sessionId;
    private final Long userId;
    private final String question;
    private final String answer;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer latencyMs;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private QAHistoryDTO(Builder builder) {
        this.id = builder.id;
        this.sessionId = builder.sessionId;
        this.userId = builder.userId;
        this.question = builder.question;
        this.answer = builder.answer;
        this.promptTokens = builder.promptTokens;
        this.completionTokens = builder.completionTokens;
        this.latencyMs = builder.latencyMs;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSessionId() {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String sessionId;
        private Long userId;
        private String question;
        private String answer;
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer latencyMs;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder question(String question) {
            this.question = question;
            return this;
        }

        public Builder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public Builder promptTokens(Integer promptTokens) {
            this.promptTokens = promptTokens;
            return this;
        }

        public Builder completionTokens(Integer completionTokens) {
            this.completionTokens = completionTokens;
            return this;
        }

        public Builder latencyMs(Integer latencyMs) {
            this.latencyMs = latencyMs;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public QAHistoryDTO build() {
            return new QAHistoryDTO(this);
        }
    }
}
