package com.ai.qa.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Document {

    @EmbeddedId
    private DocumentId id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Kind kind;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static Document create(String documentId, Long userId, String title, String content, Kind kind) {
        LocalDateTime now = LocalDateTime.now();
        return Document.builder()
                .id(new DocumentId(documentId, now))
                .userId(userId)
                .title(title)
                .content(content)
                .kind(kind)
                .build();
    }

    public enum Kind {
        TEXT,
        CODE,
        IMAGE,
        SHEET
    }

    public String getDocumentId() {
        return id.getDocumentId();
    }

    public LocalDateTime getCreatedAt() {
        return id.getCreatedAt();
    }
}

