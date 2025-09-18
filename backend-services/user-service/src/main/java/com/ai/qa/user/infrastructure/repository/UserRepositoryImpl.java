package com.ai.qa.user.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.infrastructure.entity.UserPO;
import com.ai.qa.user.infrastructure.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private JpaUserRepository jpaUserRepository;
    private UserMapper mapper;

    @Override
    public void save(User user) {
        jpaUserRepository.save(mapper.toUserOP(user));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserPO userpo = jpaUserRepository.findByUsername(username);
        return Optional.ofNullable(mapper.toUser(userpo));
    }

    @Override
    public Optional<User> findById(Long userId) {
        Optional<UserPO> userpo = jpaUserRepository.findById(userId);
        return Optional.ofNullable(mapper.toUser(userpo.get()));
    }
}
