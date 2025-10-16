package com.ai.qa.service.application.dto;

public class SaveHistoryCommand {
    private final String sessionId;
    private final Long userId;
    private final String question;
    private final String answer;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer latencyMs;

    private SaveHistoryCommand(Builder builder) {
        this.sessionId = builder.sessionId;
        this.userId = builder.userId;
        this.question = builder.question;
        this.answer = builder.answer;
        this.promptTokens = builder.promptTokens;
        this.completionTokens = builder.completionTokens;
        this.latencyMs = builder.latencyMs;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String sessionId;
        private Long userId;
        private String question;
        private String answer;
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer latencyMs;

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

        public SaveHistoryCommand build() {
            return new SaveHistoryCommand(this);
        }
    }
}
