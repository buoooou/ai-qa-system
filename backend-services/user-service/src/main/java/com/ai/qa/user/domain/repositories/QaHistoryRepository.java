package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.QaHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface QaHistoryRepository {

    QaHistory save(QaHistory history);

    List<QaHistory> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    List<QaHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<QaHistory> findBySessionIdOrderByCreatedAtDesc(Long sessionId);

    void deleteBySessionIdAndCreatedAtAfter(Long sessionId, LocalDateTime createdAfter);
}
