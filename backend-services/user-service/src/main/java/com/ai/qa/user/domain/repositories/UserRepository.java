package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    /**
     * Load a user by primary key.
     */
    Optional<User> findById(Long id);

    /**
     * Find a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email.
     */
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);
}
