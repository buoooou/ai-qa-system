package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.domain.model.QaSession;
import com.ai.qa.user.domain.repositories.QaSessionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaSessionJpaRepository extends JpaRepository<QaSession, Long>, QaSessionRepository {

    @Override
    List<QaSession> findByUserIdOrderByCreatedAtDesc(Long userId);
}
