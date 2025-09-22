// com/ai/qa/user/domain/repo/UserRepository.java
package com.ai.qa.user.domain.repo;

import com.ai.qa.user.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long userId);  // 修正返回值为Optional
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}