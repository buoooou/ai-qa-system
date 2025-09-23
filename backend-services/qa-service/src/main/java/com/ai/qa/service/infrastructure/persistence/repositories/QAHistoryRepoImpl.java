package com.ai.qa.service.infrastructure.persistence.repositories;

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

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    @Override
    public QAHistory save(QAHistory history) {
        QAHistoryPO po = toPO(history);
        QAHistoryPO savedPO = jpaQAHistoryRepository.save(po);
        return toDomain(savedPO);
    }

    @Override
    public Optional<QAHistory> findHistoryById(String id) {
        Optional<QAHistoryPO> po = jpaQAHistoryRepository.findById(Long.parseLong(id));
        return po.map(this::toDomain);
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        List<QAHistoryPO> poList = jpaQAHistoryRepository.findBySessionId(sessionId);
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QAHistory> findHistoryByUserId(String userId) {
        List<QAHistoryPO> poList = jpaQAHistoryRepository.findByUserId(userId);
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaQAHistoryRepository.deleteById(Long.parseLong(id));
    }

    /**
     * 将领域模型转换为持久化对象
     */
    private QAHistoryPO toPO(QAHistory history) {
        QAHistoryPO po = new QAHistoryPO();
        po.setId(history.getId());
        po.setUserId(history.getUserId());
        po.setQuestion(history.getQuestion());
        po.setAnswer(history.getAnswer());
        po.setCreateTime(history.getTimestamp());
        po.setSessionId(history.getSessionId());
        return po;
    }

    /**
     * 将持久化对象转换为领域模型
     */
    private QAHistory toDomain(QAHistoryPO po) {
        QAHistory history = new QAHistory(po.getId());
        history.setUserId(po.getUserId());
        history.setQuestion(po.getQuestion());
        history.setAnswer(po.getAnswer());
        history.setCreateTime(po.getCreateTime());
        history.setSessionId(po.getSessionId());
        return history;
    }
}
