package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.infrastructure.persistence.entities.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserPO, Long> {
    Optional<UserPO> findByUsername(String username);  // 修正返回值为UserPO
    boolean existsByUsername(String username);
}