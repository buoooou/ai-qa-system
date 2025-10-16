package com.ai.qa.service.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatCompletionCommand {
    private final Long userId;
    private final String sessionId;
    private final String sessionTitle;
    private final String question;
    private final List<HistoryMessage> history;

    private ChatCompletionCommand(Builder builder) {
        this.userId = builder.userId;
        this.sessionId = builder.sessionId;
        this.sessionTitle = builder.sessionTitle;
        this.question = builder.question;
        this.history = Collections.unmodifiableList(new ArrayList<>(builder.history));
    }

    public Long getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public String getQuestion() {
        return question;
    }

    public List<HistoryMessage> getHistory() {
        return history;
    }

    public ChatCompletionCommand withSessionId(String resolvedSessionId) {
        return builder()
                .userId(userId)
                .sessionId(resolvedSessionId)
                .sessionTitle(sessionTitle)
                .question(question)
                .history(history)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private String sessionId;
        private String sessionTitle;
        private String question;
        private List<HistoryMessage> history = new ArrayList<>();

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder sessionTitle(String sessionTitle) {
            this.sessionTitle = sessionTitle;
            return this;
        }

        public Builder question(String question) {
            this.question = question;
            return this;
        }

        public Builder history(List<HistoryMessage> history) {
            this.history = history == null ? new ArrayList<>() : new ArrayList<>(history);
            return this;
        }

        public ChatCompletionCommand build() {
            return new ChatCompletionCommand(this);
        }
    }

    public static class HistoryMessage {
        private final String role;
        private final String content;

        private HistoryMessage(Builder builder) {
            this.role = builder.role;
            this.content = builder.content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String role;
            private String content;

            public Builder role(String role) {
                this.role = role;
                return this;
            }

            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public HistoryMessage build() {
                return new HistoryMessage(this);
            }
        }
    }
}
