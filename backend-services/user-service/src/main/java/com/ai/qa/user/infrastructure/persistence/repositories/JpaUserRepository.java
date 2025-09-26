package com.ai.qa.user.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ai.qa.user.infrastructure.persistence.entities.User;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}