package com.ai.qa.user.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.qa.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    Optional<User> findByUsernameAndPassword(String username, String password);
}
