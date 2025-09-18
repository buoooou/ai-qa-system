package com.ai.qa.service.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mappers.QAHistoryMapper;

@Repository
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;
    private QAHistoryMapper mapper;

    @Override
    public void save(QAHistory history) {
        QAHistoryPO qaHistoryPO = mapper.toPO(history);
        jpaQAHistoryRepository.save(qaHistoryPO);
    }

    @Override
    public int delete(String sessionId){
        return jpaQAHistoryRepository.deleteBySessionId(sessionId);
    }

    @Override
    public List<QAHistoryPO> findBySessionId(String sessionId) {
        return jpaQAHistoryRepository.findBySessionId(sessionId);
    }
}
