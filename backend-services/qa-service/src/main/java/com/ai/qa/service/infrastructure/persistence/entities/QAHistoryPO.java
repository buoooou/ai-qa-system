package com.ai.qa.service.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name= "qa_history")
public class QAHistoryPO {
    @Id
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "question")
    private String question;
    @Column(name = "answer")
    private String answer;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "create_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
    @Column(name = "update_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime updateTime;
}
