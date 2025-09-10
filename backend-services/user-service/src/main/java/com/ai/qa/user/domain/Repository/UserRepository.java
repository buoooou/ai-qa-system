package com.ai.qa.user.domain.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ai.qa.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
