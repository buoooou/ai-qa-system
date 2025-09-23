package com.ai.qa.service.domain.model;

import lombok.Getter;

/**
 * 历史会话实体类，用于存储用户会话历史信息。
 */
@Getter
public class QAHistorySession {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 会话主题
     */
    private String topic;

    private QAHistorySession(String userId, String sessionId, String topic) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.topic = topic;
    }

    /**
     * 创建历史会话实例的静态工厂方法。
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param topic 会话主题
     * @return 历史会话实例
     */
    public static QAHistorySession getInstance(String userId, String sessionId, String topic){
        return new QAHistorySession(userId, sessionId, topic);
    }
}
