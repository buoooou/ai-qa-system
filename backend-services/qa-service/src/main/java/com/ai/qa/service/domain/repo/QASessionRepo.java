package com.ai.qa.service.domain.repo;

import java.util.List;

import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.infrastructure.persistence.entities.QASessionPO;

public interface QASessionRepo {
    // 保存QA会话
    void save(QAHistorySession session);
    // 删除QA会话
    int delete(String sessionId);
    // 根据UserId获取历史Session
    List<QASessionPO> findByUserId(String userId);
}
