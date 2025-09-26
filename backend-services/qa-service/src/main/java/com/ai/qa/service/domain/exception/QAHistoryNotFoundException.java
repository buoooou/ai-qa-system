package com.ai.qa.service.domain.exception;

/**
 * 未找到问答历史记录时抛出的异常
 */
public class QAHistoryNotFoundException extends RuntimeException {
    public QAHistoryNotFoundException(String sessionId) {
        super("未找到会话ID为 " + sessionId + " 的问答历史记录");
    }

    public QAHistoryNotFoundException(String sessionId, Throwable cause) {
        super("未找到会话ID为 " + sessionId + " 的问答历史记录", cause);
    }
}

