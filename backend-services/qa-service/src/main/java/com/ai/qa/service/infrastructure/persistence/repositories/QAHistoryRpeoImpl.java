package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mappers.QAHistoryMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QAHistoryRpeoImpl implements QAHistoryRepo {

    private final JpaHistoryRepository jpaRepository;
    private final QAHistoryMapper qaMapper;


    @Override
    public void save(QAHistory history) {
        QAHistoryPO historyPO = qaMapper.toPo(history);
        jpaRepository.save(historyPO);
    }

    @Override
    public Optional<QAHistory> findHistoryById(Long id) {
        return jpaRepository.findById(id)
                .map(qaMapper::toDomain);
    }

//    @Override
//    public List<QAHistory> findHistoryBySessionId(String sessionId) {
//        return jpaRepository.findBySessionId(sessionId).stream()
//                .map(qaMapper::toDomain)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<QAHistory> findHistoryByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(qaMapper::toDomain)
                .collect(Collectors.toList());
    }

}