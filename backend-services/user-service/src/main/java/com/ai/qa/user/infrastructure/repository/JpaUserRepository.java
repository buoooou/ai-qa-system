package com.ai.qa.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.qa.user.infrastructure.entity.UserPO;

public interface JpaUserRepository extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);
}
