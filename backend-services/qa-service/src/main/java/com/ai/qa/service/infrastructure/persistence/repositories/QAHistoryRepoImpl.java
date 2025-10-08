package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepository;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/**
 * QAHistoryRepository 的实现类
 * 负责领域模型与数据库实体的映射，并调用 JPA Repository 完成 CRUD
 */
@Repository
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepository {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    private final QAHistoryMapper mapper;

    @Override
    public void save(QAHistory history) {
        QAHistoryPO qaHistoryPO = mapper.toPO(history);
        jpaQAHistoryRepository.save(qaHistoryPO);
    }

    @Override
    public Optional<QAHistory> findHistoryById(String id) {
        return jpaQAHistoryRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        List<QAHistoryPO> poList = jpaQAHistoryRepository.findBySessionId(sessionId);
        return poList.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QAHistory> findHistoryByUserId(String userId) {
        List<QAHistoryPO> poList = jpaQAHistoryRepository.findByUserId(userId);
        return poList.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}