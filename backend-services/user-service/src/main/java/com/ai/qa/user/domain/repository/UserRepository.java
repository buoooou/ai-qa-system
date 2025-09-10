package com.ai.qa.user.domain.repository;


import com.ai.qa.user.domain.entity.User;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}