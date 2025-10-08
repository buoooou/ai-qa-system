package com.ai.qa.service.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "qa_history")
@Data
public class QAHistoryPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String userId;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private String sessionId;

    private LocalDateTime create_time;
    private LocalDateTime update_time;

    private String ragAnswer;

}