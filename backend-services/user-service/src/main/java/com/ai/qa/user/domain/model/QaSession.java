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
@Table(name = "qa_session")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QaSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static QaSession create(Long userId, String title) {
        LocalDateTime now = LocalDateTime.now();
        return QaSession.builder()
                .userId(userId)
                .title(title)
                .status(Status.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void rename(String newTitle) {
        this.title = newTitle;
        touch();
    }

    public void archive() {
        this.status = Status.ARCHIVED;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE,
        ARCHIVED
    }
}
