package com.ai.qa.service.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAResponse {

    /**
     * 问答记录ID
     *
     * 数据库中的唯一标识，用于后续查询和管理
     */
    private Long id;

    /**
     * 用户ID
     *
     * 标识这个问答属于哪个用户
     */
    private Long userId;

    /**
     * 用户提出的问题
     */
    private String question;

    /**
     * AI返回的回答
     */
    private String answer;

    private String sessionId;

    /**
     * 问答时间
     */
    private LocalDateTime createTime;

    /**
     * AI模型信息（可选）
     *
     * 用于标识使用了哪个AI模型进行回答
     */
    private String model;

    /**
     * 响应时间（毫秒）
     *
     * 用于性能监控和用户体验优化
     */
    private Long responseTime;

    /**
     * 构造函数 - 用于从数据库记录创建响应
     * 
     * @param id 问答记录ID
     * @param userId 用户ID
     * @param question 用户问题
     * @param answer AI回答
     * @param createTime 创建时间
     */
    public QAResponse(Long id, Long userId, String question, String answer, String sessionId, LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.sessionId = sessionId;
        this.createTime = createTime;
    }

}
