package com.ai.qa.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Gemini 请求数据传输对象，用于封装 Gemini 服务请求所需的信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {
    /**
     * 模型名称
     */
    private String model;
    /**
     * 消息列表
     */
    private List<Message> messages;
    /**
     * 温度参数
     */
    private double temperature;

    /**
     * 消息数据传输对象，用于封装消息内容。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息角色
         */
        private String role;
        /**
         * 消息内容
         */
        private String content;
    }
}
