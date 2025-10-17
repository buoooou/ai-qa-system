package com.ai.qa.service.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

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
