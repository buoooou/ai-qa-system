package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, Long> {

    List<QAHistoryPO> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<QAHistoryPO> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    void deleteBySessionId(Long sessionId);
}
