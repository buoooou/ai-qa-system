package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.QaSession;

import java.util.List;
import java.util.Optional;

public interface QaSessionRepository {

    QaSession save(QaSession session);

    Optional<QaSession> findById(Long id);

    List<QaSession> findByUserIdOrderByCreatedAtDesc(Long userId);
}
