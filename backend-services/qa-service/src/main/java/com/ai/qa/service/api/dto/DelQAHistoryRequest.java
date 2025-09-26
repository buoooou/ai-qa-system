package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 删除历史会话请求数据传输对象，用于封装删除历史会话所需的信息。
 */
@Data
public class DelQAHistoryRequest {
    /**
     * 会话ID
     */
    private String sessionId;
}
