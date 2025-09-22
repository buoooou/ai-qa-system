package com.ai.qa.service.domain.model;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class QAHistory {

    private final Long id;
    private final Long userId;
    private final String sessionId;
    private final String question;
    private final String answer;
    private final LocalDateTime createTime;


    public QAHistory(Long id,Long userId, String sessionId, String question, String answer,LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.sessionId = sessionId;
        this.createTime = createTime;
    }

    public static QAHistory createNew(Long id, Long userId, String sessionId, String question, String answer) {
        return new QAHistory(
                id,
                userId,
                sessionId,
                question,
                answer,
                LocalDateTime.now()
        );
    }
}