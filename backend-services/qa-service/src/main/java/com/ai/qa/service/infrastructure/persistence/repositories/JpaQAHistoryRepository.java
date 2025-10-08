package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, String> {

    QAHistoryPO findHistoryById(String userId);

    // 根据用户ID查找历史记录
    List<QAHistoryPO> findByUserId(String userId);

    // 根据 sessionId 查找历史记录
    List<QAHistoryPO> findBySessionId(String sessionId);

}