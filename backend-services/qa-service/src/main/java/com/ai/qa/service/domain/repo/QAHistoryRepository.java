package com.ai.qa.service.domain.repo;

import com.ai.qa.service.domain.model.QAHistory;

import java.util.List;
import java.util.Optional;

public interface QAHistoryRepository {

    void save(QAHistory history);

    Optional<QAHistory> findHistoryById(String id);

    List<QAHistory> findHistoryBySession(String sessionId);

    List<QAHistory> findHistoryByUserId(String userId);

}