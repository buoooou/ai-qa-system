package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.infrastructure.persistence.entities.User;
import com.ai.qa.user.infrastructure.persistence.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import com.ai.qa.user.domain.repositories.UserRepositoryRepo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryRepoImpl implements UserRepositoryRepo{
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    public UserDto login(String username, String pwd){
        Optional<User> userOptional = jpaUserRepository.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(pwd)) {
            return userMapper.toUserDto(userOptional.get());
        }
        return null;
    }
    @Override
    public UserDto findByUserId(Long userId) {
        Optional<User> userOptional = jpaUserRepository.findById(userId);
        if(userOptional.isPresent()){
            return userMapper.toUserDto(userOptional.get());
        }
        return null;
    }

    public UserDto findByUsername(String username){
        Optional<User> userOptional = jpaUserRepository.findByUsername(username);
        if(userOptional.isPresent()){
            return userMapper.toUserDto(userOptional.get());
        }
        return null;
    }

    public UserDto register(UserDto userDto){
        User toRegister = userMapper.toUser(userDto);
        User registeredUser = jpaUserRepository.save(toRegister);
        return userMapper.toUserDto(registeredUser);
    }

    public int updateNick(String nick, String username){
        Optional<User> userOptional = jpaUserRepository.findByUsername(username);
        if (userOptional.isPresent() && nick != null && !nick.isEmpty()) {
            User user = userOptional.get();
            user.setNickname(nick);
            user.setUpdateTime(LocalDateTime.now());
            jpaUserRepository.save(user);
            return 1;
        }
        return 0;
    }
}
