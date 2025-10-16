package com.ai.qa.service.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qa_history_hl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QAHistoryPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private String answer;

    @Column(name = "session_id", nullable = false, length = 255)
    private String sessionId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    // private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

}
