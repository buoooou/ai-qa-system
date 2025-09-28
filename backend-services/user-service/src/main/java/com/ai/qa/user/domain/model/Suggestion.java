package com.ai.qa.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name = "document_id", nullable = false, length = 36)
    private String documentId;

    @Column(name = "document_created_at", nullable = false)
    private LocalDateTime documentCreatedAt;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "suggested_text", nullable = false, columnDefinition = "TEXT")
    private String suggestedText;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_resolved", nullable = false)
    private boolean resolved;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Suggestion create(String documentId, LocalDateTime documentCreatedAt,
                                    String originalText, String suggestedText,
                                    String description, Long userId) {
        return Suggestion.builder()
                .documentId(documentId)
                .documentCreatedAt(documentCreatedAt)
                .originalText(originalText)
                .suggestedText(suggestedText)
                .description(description)
                .resolved(false)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void resolve() {
        this.resolved = true;
    }
}

