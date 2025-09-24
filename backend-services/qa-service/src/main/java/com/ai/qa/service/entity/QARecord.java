package com.ai.qa.service.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "qa_records")
@EntityListeners(AuditingEntityListener.class)
public class QARecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(name = "answer", columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "question_type", length = 50)
    private String questionType;

    @Column(name = "response_time")
    private Long responseTime;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
