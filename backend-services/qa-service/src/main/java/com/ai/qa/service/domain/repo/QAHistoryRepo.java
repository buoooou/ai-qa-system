package com.ai.qa.service.domain.repo;

import com.ai.qa.service.domain.model.QAHistory;

import java.util.List;

public interface QAHistoryRepo {

    QAHistory save(QAHistory history);

    List<QAHistory> findByUserAndSession(Long userId, Long sessionId, Integer limit);
}
