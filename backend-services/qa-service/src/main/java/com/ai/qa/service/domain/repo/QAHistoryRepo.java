package com.ai.qa.service.domain.repo;

import java.util.List;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

public interface QAHistoryRepo {
    // 保存QA会话
    void save(QAHistory history);
    // 删除QA会话
    int delete(String sessionId);
    // 根据SessionID查询问答历史
    List<QAHistoryPO> findBySessionId(String sessionId);
}
