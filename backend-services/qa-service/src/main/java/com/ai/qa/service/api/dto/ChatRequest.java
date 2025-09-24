package com.ai.qa.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天请求数据传输对象，用于封装聊天消息内容。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    /**
     * 聊天消息内容
     */
    private String message;
}
