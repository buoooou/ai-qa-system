package com.ai.qa.service.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ai.qa.service.infrastructure.persistence.entities.UserPO;

@Repository
public interface JpaUserRepository extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);
}
