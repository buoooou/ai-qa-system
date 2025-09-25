package com.ai.qa.service.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity      
@Data
public class QAHistory {
    @Id
    private Long id;
    private Long userId;
//    private String sessionId;
    private String question;
    private String answer;
    private LocalDateTime createTime;

    public QAHistory(Long userId, String question, String answer, LocalDateTime createTime) {
//        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
//        this.sessionId = sessionId;
        this.createTime = createTime;
    }

    public static QAHistory createNew(Long userId, String question, String answer) {
        return new QAHistory(
//                id,
                userId,
                question,
                answer,
//                sessionId,
                LocalDateTime.now()
        );
    }
 }
