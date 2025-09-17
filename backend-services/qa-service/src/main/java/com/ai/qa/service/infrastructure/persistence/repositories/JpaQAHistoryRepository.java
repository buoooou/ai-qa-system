package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, Long> {

    QAHistoryPO findHistoryById(Long id);
    List<QAHistoryPO> findHistoryByUserId(String userid);
    List<QAHistoryPO> findHistoryBySessionId(String sessionid);
}
