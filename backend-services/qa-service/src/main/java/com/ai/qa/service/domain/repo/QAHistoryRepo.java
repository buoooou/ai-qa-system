package com.ai.qa.service.domain.repo;

import java.util.List;
import java.util.Optional;

import com.ai.qa.service.domain.model.QAHistory;

public interface QAHistoryRepo {

    void save(QAHistory history);

    Optional<QAHistory> findHistoryById(Long id);
    
    List<QAHistory> findHistoryByUserId(Long userId);
    
//    List<QAHistory> findHistoryBySessionId(String sessionId);
}
