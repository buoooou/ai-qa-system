package com.ai.qa.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Gemini 响应数据传输对象，用于封装 Gemini 服务响应内容。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {
    /**
     * 响应ID
     */
    private String id;
    /**
     * 对象类型
     */
    private String object;
    /**
     * 创建时间戳
     */
    private long created;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 选择列表
     */
    private List<Choice> choices;

    /**
     * 选择数据传输对象，用于封装选择内容。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        /**
         * 选择索引
         */
        private int index;
        /**
         * 消息内容
         */
        private Message message;
        /**
         * 完成原因
         */
        private String finish_reason;
    }

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
