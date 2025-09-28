package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 历史会话请求数据传输对象，用于封装获取历史会话所需的信息。
 */
@Data
public class QAHistoryRequest {
    /**
     * 会话ID
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
    /**
     * 指定的历史会话SessionId
     */
    private String specifySId;
}
