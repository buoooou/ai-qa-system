package com.ai.qa.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "qa_history")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QaHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static QaHistory create(String sessionId, Long userId, String question) {
        LocalDateTime now = LocalDateTime.now();
        return QaHistory.builder()
                .sessionId(sessionId)
                .userId(userId)
                .question(question)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateAnswer(String answer, Integer completionTokens, Integer latencyMs) {
        this.answer = answer;
        if (completionTokens != null) {
            this.completionTokens = completionTokens;
        }
        if (latencyMs != null) {
            this.latencyMs = latencyMs;
        }
        touch();
    }

    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
