package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 创建新会话请求数据传输对象，用于封装创建新会话所需的信息。
 */
@Data
public class CreateNewSessionRequest {
    /**
     * 前轮会话的SessionId
     */
    private String sessionId;
    /**
     * 用户ID
     */
    private String userid;
    /**
     * 会话主题
     */
    private String topic;
}
