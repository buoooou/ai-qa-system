package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, String> {

    List<QAHistoryPO> findBySessionId(String sessionId);

    int deleteBySessionId(String sessionId);
}
