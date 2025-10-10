package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 创建会话请求数据传输对象，用于封装创建会话所需的信息。
 */
@Data
public class CreateSessionRequest {
    /**
     * 用户ID
     */
    private String userid;
}
