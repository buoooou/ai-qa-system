package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.domain.repo.QASessionRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QASessionPO;
import com.ai.qa.service.infrastructure.persistence.mappers.QASessionMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QASessionRepoImpl implements QASessionRepo {

    private final JpaQASessionRepository jpaQASessionRepository;
    private QASessionMapper mapper;

    @Override
    public void save(QAHistorySession session) {
        jpaQASessionRepository.save(mapper.toPO(session));
    }
    @Override
    public int delete(String sessionId) {
        return jpaQASessionRepository.deleteBySessionId(sessionId);
    }
    @Override
    public List<QASessionPO> findByUserId(String userId) {
        return jpaQASessionRepository.findByUserId(userId);
    }
}
