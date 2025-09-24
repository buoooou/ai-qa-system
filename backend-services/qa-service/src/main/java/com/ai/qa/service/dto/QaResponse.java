package com.ai.qa.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 问答响应DTO
 *
 * 用于返回AI问答的结果信息
 * 包含问题、答案、时间戳等信息
 *
 * @author David
 * @version 1.0
 * @since 2025-09-06
 */
@Data                    // Lombok注解：自动生成getter、setter、toString等方法
@NoArgsConstructor       // Lombok注解：生成无参构造函数
@AllArgsConstructor      // Lombok注解：生成全参构造函数
public class QaResponse {

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
     * 构造函数 - 用于创建基本的问答响应
     *
     * @param question 用户问题
     * @param answer AI回答
     */
    public QaResponse(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 构造函数 - 用于创建完整的问答响应
     *
     * @param userId 用户ID
     * @param question 用户问题
     * @param answer AI回答
     */
    public QaResponse(Long userId, String question, String answer) {
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 构造函数 - 用于从数据库记录创建响应
     *
     * @param id 问答记录ID
     * @param userId 用户ID
     * @param question 用户问题
     * @param answer AI回答
     * @param createTime 创建时间
     */
    public QaResponse(Long id, Long userId, String question, String answer, LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.createTime = createTime;
    }
}