package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.domain.model.QaHistory;
import com.ai.qa.user.domain.repositories.QaHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaHistoryJpaRepository extends JpaRepository<QaHistory, Long>, QaHistoryRepository {

    @Override
    List<QaHistory> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    @Override
    List<QaHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
