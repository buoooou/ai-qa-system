package com.ai.qa.qa.application;

import com.ai.qa.qa.api.dto.QARequest;
import com.ai.qa.qa.api.dto.QAResponse;
import com.ai.qa.qa.domain.entity.QA;
import java.util.List;

public interface QAService {
    /**
     * 提出问题并获取答案
     * @param request 问题请求
     * @return 问答响应
     */
    QAResponse askQuestion(QARequest request);
    
    /**
     * 根据用户ID获取问答历史
     * @param userId 用户ID
     * @return 问答历史列表
     */
    List<QAResponse> getQAHistory(Long userId);
    
    /**
     * 根据ID获取特定问答记录
     * @param id 问答记录ID
     * @return 问答记录
     */
    QA getQAById(Long id);
}