package com.ai.qa.service.application.dto;

public class QAHistoryQuery {
    private final Long userId;
    private final String sessionId;
    private final Integer limit;

    private QAHistoryQuery(Builder builder) {
        this.userId = builder.userId;
        this.sessionId = builder.sessionId;
        this.limit = builder.limit;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getLimit() {
        return limit;
    }

    public boolean requiresOwnershipCheck() {
        return userId != null && sessionId != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private String sessionId;
        private Integer limit;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public QAHistoryQuery build() {
            return new QAHistoryQuery(this);
        }
    }
}
