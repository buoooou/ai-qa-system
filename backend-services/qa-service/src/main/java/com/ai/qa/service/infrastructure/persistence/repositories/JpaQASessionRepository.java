package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ai.qa.service.infrastructure.persistence.entities.QASessionPO;

@Repository
public interface JpaQASessionRepository extends JpaRepository<QASessionPO, String>{
    List<QASessionPO> findByUserId(String userId);
    int deleteBySessionId(String sessionId);
}
