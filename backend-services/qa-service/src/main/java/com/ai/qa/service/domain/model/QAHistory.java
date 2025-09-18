package com.ai.qa.service.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class QAHistory {

    private String userId;
    private String sessionId;
    private String question;
    private String answer;
    private LocalDateTime createTime;

    private QAHistory(String userId, String sessionId, String question, String answer, LocalDateTime createTime) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.question = question;
        this.answer = answer;
        this.createTime = createTime;
    }

    public static QAHistory getInstance(String userId, String sessionId, String question, String answer, LocalDateTime createTime){
        return new QAHistory(userId, sessionId, question, answer, createTime);
    }

    public void validate() {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("问题不能为空");
        }
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("答案不能为空");
        }
    }
}
