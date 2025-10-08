package com.ai.qa.service.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * QAHistory 数据传输对象
 */
@Data
public class QAHistoryDTO {

    /** 唯一ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** 会话ID */
    private String sessionId;

    /** 用户提问 */
    private String question;

    /** AI 返回的基础答案 */
    private String answer;

    /** 基于 RAG 增强后的答案（如果存在 RAG） */
    private String ragAnswer;

    /** 时间戳 */
    private LocalDateTime timestamp;
}
