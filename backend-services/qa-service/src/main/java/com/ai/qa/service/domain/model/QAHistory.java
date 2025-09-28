package com.ai.qa.service.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * 问答历史记录实体类，用于存储用户问答历史信息。
 */
@Getter
public class QAHistory {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 问题内容
     */
    private String question;
    /**
     * 回答内容
     */
    private String answer;
    /**
     * 创建时间
     */
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
