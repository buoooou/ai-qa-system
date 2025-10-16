package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.domain.model.QaHistory;
import com.ai.qa.user.domain.repositories.QaHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QaHistoryJpaRepository extends JpaRepository<QaHistory, Long>, QaHistoryRepository {

    @Override
    List<QaHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Override
    List<QaHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Override
    List<QaHistory> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    @Override
    @Modifying
    @Query("delete from QaHistory h where h.sessionId = :sessionId and h.createdAt >= :createdAfter")
    void deleteBySessionIdAndCreatedAtAfter(String sessionId, LocalDateTime createdAfter);

    @Override
    @Modifying
    @Query("delete from QaHistory h where h.sessionId = :sessionId")
    void deleteBySessionId(String sessionId);
}
