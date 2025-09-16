package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;

import java.util.List;
import java.util.Optional;

public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    public QAHistoryRepoImpl(JpaQAHistoryRepository repo) {
        this.jpaQAHistoryRepository = repo;
    }

    @Override
    public void save(QAHistory history) {
        // 这里需要实现 domain->PO 的属性映射
        // QAHistoryPO po = mapper.toPO(history);
        // jpaQAHistoryRepository.save(po);
        // 简化处理，略
    }

    @Override
    public Optional<QAHistory> findHistoryById(String id) {
        // Long idLong = Long.valueOf(id);
        // QAHistoryPO po = jpaQAHistoryRepository.findHistoryById(idLong);
        // return Optional.ofNullable(mapper.toDomain(po));
        return Optional.empty();
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        // TODO: 按 sessionId 查询
        return List.of();
    }
}