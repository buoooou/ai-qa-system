package com.ai.qa.service.domain.model;

import lombok.Getter;

@Getter
public class QAHistorySession {
    private String userId;
    private String sessionId;
    private String topic;

    private QAHistorySession(String userId, String sessionId, String topic) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.topic = topic;
    }

    public static QAHistorySession getInstance(String userId, String sessionId, String topic){
        return new QAHistorySession(userId, sessionId, topic);
    }
}
