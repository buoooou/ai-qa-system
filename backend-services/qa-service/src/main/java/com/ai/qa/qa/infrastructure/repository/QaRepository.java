package com.ai.qa.qa.infrastructure.repository;

import com.ai.qa.qa.domain.entity.QA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QaRepository extends JpaRepository<QA, Long> {
    List<QA> findByUserId(Long userId);
}