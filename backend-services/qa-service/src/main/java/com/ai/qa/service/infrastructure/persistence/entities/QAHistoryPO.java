// com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO.java
package com.ai.qa.service.infrastructure.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_history")
@Getter
@Setter
public class QAHistoryPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private String answer;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createTime;
}