// com.ai.qa.service.infrastructure.persistence.QAHistoryRepoImpl.java
package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.mapper.QAMapper;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaRepository;
    private final QAMapper qaMapper;

    @Override
    public void save(QAHistory history) {
        QAHistoryPO po = qaMapper.toPo(history);
        jpaRepository.save(po);
    }

    @Override
    public Optional<QAHistory> findHistoryById(Long id) {
        return jpaRepository.findById(id)
                .map(qaMapper::toDomain);
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        return jpaRepository.findBySessionId(sessionId).stream()
                .map(qaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QAHistory> findHistoryByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(qaMapper::toDomain)
                .collect(Collectors.toList());
    }
}