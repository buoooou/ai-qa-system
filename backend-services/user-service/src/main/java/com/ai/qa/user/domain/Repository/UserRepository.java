package com.ai.qa.user.domain.Repository;

import java.util.Optional;
import com.ai.qa.user.domain.model.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
}
