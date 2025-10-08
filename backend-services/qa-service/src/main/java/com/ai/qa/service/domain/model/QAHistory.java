package com.ai.qa.service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * QAHistory 领域模型，用于记录一次问答历史
 */
@Getter
@Builder
public class QAHistory {

    private final String id;
    private final String userId;
    private final String sessionId;
    private final String question;
    private final String answer;
    private final LocalDateTime timestamp;

    private final QARAG rag; // 可选的 RAG 上下文对象

    // 构造函数私有，使用静态工厂方法创建
    public QAHistory(String id, String userId, String sessionId, String question,
            String answer, LocalDateTime timestamp, QARAG rag) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
        this.rag = rag;
    }

    // 静态工厂方法：创建新的问答历史
    public static QAHistory createNew(String userId, String sessionId, String question, String answer, QARAG rag) {
        return QAHistory.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .sessionId(sessionId)
                .question(question)
                .answer(answer)
                .timestamp(LocalDateTime.now())
                .rag(rag)
                .build();
    }

    // 返回基础问答答案
    public String getAnswer() {
        return this.answer;
    }

    // 如果存在 RAG 上下文，返回增强后的答案
    public String getRAGAnswer() {
        if (rag != null) {
            return answer + " " + rag.getGeneratedAnswer();
        }
        return answer;
    }
}
