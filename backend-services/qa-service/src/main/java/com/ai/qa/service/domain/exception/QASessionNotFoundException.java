package com.ai.qa.service.domain.exception;

public class QASessionNotFoundException extends RuntimeException {
    public QASessionNotFoundException(String userId) {
        super("未找到会话UserID为 " + userId + " 的问答会话历史记录");
    }

    public QASessionNotFoundException(String userId, Throwable cause) {
        super("未找到会话ID为 " + userId + " 的问答会话历史记录", cause);
    }
}
