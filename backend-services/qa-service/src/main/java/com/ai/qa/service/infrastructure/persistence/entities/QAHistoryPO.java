package com.ai.qa.service.infrastructure.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name= "qa_history")
public class QAHistoryPO {

    private Long id;
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
    private LocalDateTime createTime;
}
