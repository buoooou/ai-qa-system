package com.ai.qa.service.application.dto;

import lombok.Data;

/**
 * 保存 QA 历史记录的命令对象
 * 
 * 该对象从 API 层接收请求后，传入应用层服务，用于构造领域模型 QAHistory
 */
@Data
public class SaveHistoryCommand {
    private Long userId;
    private String question;
    private String answer;
    private String sessionId;
    private String ragAnswer; // 可选 RAG 增强答案

    /**
     * 全参数构造函数
     *
     * @param userId    用户 ID
     * @param question  用户问题
     * @param answer    AI 原始答案
     * @param ragAnswer RAG 增强答案，可为空
     * @param sessionId 会话 ID，可为空
     */
    public SaveHistoryCommand(Long userId, String question, String answer, String ragAnswer, String sessionId) {
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.ragAnswer = ragAnswer;
        this.sessionId = sessionId;
    }

    /**
     * 简化构造函数，只保留必填字段
     *
     * @param userId   用户 ID
     * @param question 用户问题
     * @param answer   AI 原始答案
     */
    public SaveHistoryCommand(Long userId, String question, String answer) {
        this(userId, question, answer, null, null);
    }
}