package com.ai.qa.service.domain.service;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;
import com.ai.qa.service.infrastructure.persistence.repositories.JpaQAHistoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    private QAHistoryMapper qaHistoryMapper;

    @Override
    public void save(QAHistory history) {
        Long id = history.getId();
        if (id != null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        String userId = history.getUserId();
        if (userId != null && userId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        QAHistoryPO qaHistoryPO = qaHistoryMapper.toPO(history);
        jpaQAHistoryRepository.save(qaHistoryPO);
    }

    @Override
    public Optional<QAHistory> findHistoryById(Long id) {
        if (id != null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        QAHistoryPO qaHistoryPO = jpaQAHistoryRepository.findHistoryById(id);
        return Optional.ofNullable(qaHistoryMapper.toDomain(qaHistoryPO));
    }

    @Override
    public List<QAHistory> findHistoryByUserId(String userId) {
        if (userId != null && userId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        List<QAHistoryPO> qaHistoryPO = jpaQAHistoryRepository.findHistoryByUserId(userId);
        return qaHistoryMapper.toDomainList(qaHistoryPO);
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        if (sessionId != null && sessionId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        List<QAHistoryPO> qaHistoryPOList = jpaQAHistoryRepository.findHistoryBySessionId(sessionId);
        return qaHistoryMapper.toDomainList(qaHistoryPOList);
    }
}