package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

    @Override
    Optional<User> findByUsername(String username);

    @Override
    Optional<User> findByEmail(String email);

    @Override
    boolean existsByUsername(String username);

    @Override
    boolean existsByEmail(String email);
}
