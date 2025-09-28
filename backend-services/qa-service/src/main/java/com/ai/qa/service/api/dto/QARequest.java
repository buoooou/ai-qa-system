package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 问答请求数据传输对象，用于封装问答请求所需的信息。
 */
@Data
public class QARequest {
    /**
     * 本轮会话的SessionId
     */
    private String sessionId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 问题内容
     */
    private String question;
}
