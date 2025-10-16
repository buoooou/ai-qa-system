package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.application.mapper.QAHistoryMapperImpl;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-backed implementation of the QA history repository.
 */
@Repository
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaRepository;
    private final QAHistoryMapperImpl mapper;

    /**
     * Persists a domain history aggregate using JPA.
     *
     * @param history domain aggregate
     * @return saved aggregate
     */
    @Override
    public QAHistory save(QAHistory history) {
        QAHistoryPO po = mapper.toEntity(history);
        QAHistoryPO saved = jpaRepository.save(po);
        return mapper.toDomain(saved);
    }

    /**
     * Retrieves histories by user or session with optional limit.
     *
     * @param userId    user identifier
     * @param sessionId optional session identifier
     * @param limit     optional maximum records
     * @return list of domain histories
     */
    @Override
    public List<QAHistory> findByUserAndSession(Long userId, String sessionId, Integer limit) {
        List<QAHistoryPO> poList;
        if (sessionId != null) {
            poList = jpaRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        } else {
            poList = jpaRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return poList.stream()
                .limit(limit != null ? limit : poList.size())
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        jpaRepository.deleteBySessionId(sessionId);
    }
}
