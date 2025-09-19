package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ai.qa.service.infrastructure.persistence.entities.UserPO;

@Repository
public interface JpaUserRepository extends JpaRepository<UserPO, Long> {
    Optional<UserPO> findByUsername(String username);
}