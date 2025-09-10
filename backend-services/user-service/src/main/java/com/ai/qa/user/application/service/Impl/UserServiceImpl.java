package com.ai.qa.user.application.service.Impl;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UserResponseDto;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Override
    public UserResponseDto<Long> register(RegisterReqDto request) {
        if (request == null) {
            return UserResponseDto.fail(ErrCode.INVALID_PARAMS);
        }

        String username = request.getUsername();
        String password = request.getPassword();

        validateUsername(username);
        validatePassword(password);

        String trimmedUsername = username.trim();

        if (userRepository.findByUsername(trimmedUsername).isPresent()) {
            return UserResponseDto.fail(ErrCode.USERNAME_EXISTS);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        return UserResponseDto.success(savedUser.getId());
    }
    /**
     * 用户登录
     */
    @Override
    public UserResponseDto<Long> login(LoginReqDto request) {
        if (request == null) {
            return UserResponseDto.fail(ErrCode.INVALID_PARAMS);
        }

        String username = request.getUsername();
        String password = request.getPassword();

        validateUsername(username);
        validatePassword(password);

        String trimmedUsername = username.trim();

        return userRepository.findByUsername(trimmedUsername)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> UserResponseDto.success(user.getId()))
                .orElse(UserResponseDto.fail(ErrCode.INVALID_CREDENTIALS));
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(ErrCode.USERNAME_REQUIRED);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BusinessException(ErrCode.PASSWORD_REQUIRED);
        }
    }

    /**
     * 更新当前用户昵称
     */
    @Override
    public UserResponseDto<String> updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrCode.USERNAME_NOT_FOUND));

        user.setNickname(nickname.trim());
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);

        return UserResponseDto.success(user.getNickname());
    }
}