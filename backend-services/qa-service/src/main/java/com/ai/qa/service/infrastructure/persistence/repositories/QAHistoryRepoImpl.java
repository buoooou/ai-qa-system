package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    private final QAHistoryMapper qaHistoryMapper;

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
        if (qaHistoryPO.isEmpty()) {
        	List<QAHistory> qaHistoryList = new ArrayList<>();
        	return qaHistoryList;
        }
        return qaHistoryMapper.toDomainList(qaHistoryPO);
    }

    @Override
    public QAHistory findHistoryBySession(String sessionId) {
        if (sessionId != null && sessionId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        List<QAHistoryPO> qaHistoryPOList = jpaQAHistoryRepository.findHistoryBySessionId(sessionId);
            Collections.sort(qaHistoryPOList, Comparator.comparingLong(QAHistoryPO::getId).reversed());
            QAHistoryPO qaHistoryPO = qaHistoryPOList.isEmpty() ? null : qaHistoryPOList.get(0);
        return qaHistoryMapper.toDomain(qaHistoryPO);
    }
}
