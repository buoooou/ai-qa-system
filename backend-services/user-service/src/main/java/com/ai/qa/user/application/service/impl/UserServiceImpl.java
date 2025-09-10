package com.ai.qa.user.application.service.impl;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.AuthRequestDTO;
import com.ai.qa.user.api.dto.UserDTO;
import com.ai.qa.user.api.exception.UserServiceException;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.constants.Constants;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.mapper.UserMapper;
import com.ai.qa.user.domain.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Get the detail of the user.")
    @Transactional
    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(x -> userMapper.userToUserDTO(x))
                .orElseThrow(() -> new UserServiceException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_USER_NOT_FOUND));
    }

    @Operation(summary = "Implement to update user's nickname.")
    @Transactional
    @Override
    public UserDTO updateNickname(String username, String newNickname) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserServiceException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_USER_NOT_FOUND));
        user.setNickname(newNickname);
        userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }

    @Operation(summary = "Implement to sign up a new user.")
    @Transactional
    @Override
    public UserDTO register(AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST.value(), Constants.MSG_USER_REGISTER_FAIL);
        }
        User newUser = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .nickname(request.getNickname()).build();
        userRepository.save(newUser);
        return userMapper.userToUserDTO(newUser);
    }
}
