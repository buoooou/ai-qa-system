package com.ai.qa.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应数据传输对象，用于封装聊天回复内容、状态和错误信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    /**
     * 聊天回复内容
     */
    private String reply;
    /**
     * 响应状态
     */
    private String status;
    /**
     * 错误信息（如果存在）
     */
    private String errorMessage;
}
