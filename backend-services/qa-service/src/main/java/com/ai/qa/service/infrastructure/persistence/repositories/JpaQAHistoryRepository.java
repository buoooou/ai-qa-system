package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, Long> {

    List<QAHistoryPO> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<QAHistoryPO> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    List<QAHistoryPO> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    void deleteBySessionId(String sessionId);

    @Modifying
    @Query("delete from QAHistoryPO h where h.sessionId = :sessionId and h.createdAt >= :createdAfter")
    void deleteBySessionIdAndCreatedAtAfter(String sessionId, LocalDateTime createdAfter);
}
