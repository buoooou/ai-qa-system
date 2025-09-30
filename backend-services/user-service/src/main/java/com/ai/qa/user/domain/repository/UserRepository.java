package com.ai.qa.user.domain.repository;

import com.ai.qa.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long userId);  // 修正返回值为Optional
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
