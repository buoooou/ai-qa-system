package com.ai.qa.service.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain aggregate representing a QA history message.
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QAHistory {

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

    /**
     * Factory method to create a new history entry with initial question.
     */
    public static QAHistory create(String sessionId, Long userId, String question) {
        LocalDateTime now = LocalDateTime.now();
        return QAHistory.builder()
                .sessionId(sessionId)
                .userId(userId)
                .question(question)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Records the AI answer, completion tokens, and latency.
     */
    public void recordAnswer(String answer, Integer completionTokens, Integer latencyMs) {
        this.answer = answer;
        if (completionTokens != null) {
            this.completionTokens = completionTokens;
        }
        if (latencyMs != null) {
            this.latencyMs = latencyMs;
        }
        touch();
    }

    /**
     * Updates question content and prompt token usage.
     */
    public void updateQuestion(String newQuestion, Integer promptTokens) {
        this.question = newQuestion;
        if (promptTokens != null) {
            this.promptTokens = promptTokens;
        }
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QAHistory that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
