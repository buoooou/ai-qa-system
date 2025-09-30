package com.ai.qa.service.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QAHistoryDto {

    private Long id;
    private Long userId;
    private String sessionId;
    private String question;
    private String answer;
    private LocalDateTime createTime;
    /**
     * 构造函数 - 用于从数据库记录创建响应
     * 
     * @param id 问答记录ID
     * @param userId 用户ID
     * @param question 用户问题
     * @param answer AI回答
     * @param createTime 创建时间
     */
    public QAHistoryDto(Long id, Long userId, String question, String answer, LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.createTime = createTime;
    }
}