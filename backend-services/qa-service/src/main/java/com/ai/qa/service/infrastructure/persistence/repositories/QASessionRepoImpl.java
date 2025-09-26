package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.domain.repo.QASessionRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QASessionPO;
import com.ai.qa.service.infrastructure.persistence.mappers.QASessionMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QASessionRepoImpl implements QASessionRepo {

    private final JpaQASessionRepository jpaQASessionRepository;
    private final QASessionMapper mapper;

    @Override
    public void save(QAHistorySession session) {
        jpaQASessionRepository.save(mapper.toPO(session));
    }

    @Transactional
    @Override
    public int delete(String sessionId) {
        return jpaQASessionRepository.deleteBySessionId(sessionId);
    }
    @Override
    public List<QASessionPO> findByUserId(String userId) {
        return jpaQASessionRepository.findByUserId(userId);
    }

    public boolean findBySessionId(String sessionId) {
        Optional<QASessionPO> optQA = jpaQASessionRepository.findBySessionId(sessionId);
        return optQA.isPresent();
    }
}
