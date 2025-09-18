package com.ai.qa.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ai.qa.user.infrastructure.entity.UserPO;

@Repository
public interface JpaUserRepository extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);
}
