package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 退出登录请求数据传输对象，用于封装退出登录所需的信息。
 */
@Data
public class LogoutRequest {
    /**
     * 会话ID
     */
    private String sessionId;
}
