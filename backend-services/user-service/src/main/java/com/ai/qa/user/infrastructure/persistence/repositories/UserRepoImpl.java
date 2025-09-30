package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.infrastructure.mapper.UserMapper;
import com.ai.qa.user.infrastructure.persistence.entities.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepoImpl implements UserRepository {

    private final JpaUserRepository jpaRepo;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserPO userPO = userMapper.toPO(user);
        UserPO savedPO = jpaRepo.save(userPO);
        return userMapper.toDomain(savedPO);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepo.findById(userId)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepo.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepo.existsByUsername(username);
    }
}