package com.ai.qa.service.infrastructure.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name= "qa_history")
public class QAHistoryPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
    private LocalDateTime createTime;
}
