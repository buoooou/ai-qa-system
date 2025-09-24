package com.ai.qa.service.service;

import com.ai.qa.service.dto.QuestionRequest;
import com.ai.qa.service.dto.QuestionResponse;

import java.util.List;

/**
 * 问答服务接口
 */
public interface QAService {

    /**
     * 处理用户问题
     * @param request 问题请求
     * @return 问答响应
     */
    QuestionResponse processQuestion(QuestionRequest request);

    /**
     * 获取用户问答历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 问答历史列表
     */
    List<QuestionResponse> getQuestionHistory(Long userId, int page, int size);

    /**
     * 获取会话问答记录
     * @param sessionId 会话ID
     * @return 会话问答记录
     */
    List<QuestionResponse> getSessionHistory(String sessionId);

    /**
     * 删除问答记录
     * @param qaId 问答记录ID
     */
    void deleteQuestion(Long qaId);
}
