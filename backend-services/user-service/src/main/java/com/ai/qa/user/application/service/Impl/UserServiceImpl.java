package com.ai.qa.user.application.service.Impl;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UserResponseDto;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.infrastructure.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 推荐使用 BCrypt

    public UserResponseDto register(RegisterReqDto request) {
        // 校验参数
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return UserResponseDto.fail(ErrCode.USERNAME_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return UserResponseDto.fail(ErrCode.PASSWORD_REQUIRED);
        }

        String username = request.getUsername().trim();

        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            return UserResponseDto.fail(ErrCode.USERNAME_EXISTS);
        }

        try {
            // 使用 Mapper 自动转换并处理加密、时间字段
            User user = userMapper.toUser(request);
            User savedUser = userRepository.save(user);
            return UserResponseDto.success(savedUser.getId());
        } catch (Exception e) {
            return UserResponseDto.fail(ErrCode.INTERNAL_ERROR);
        }
    }

    public UserResponseDto login(LoginReqDto request) {
        // 校验参数
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return UserResponseDto.fail(ErrCode.USERNAME_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return UserResponseDto.fail(ErrCode.PASSWORD_REQUIRED);
        }

        String username = request.getUsername().trim();
        String password = request.getPassword();

        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> UserResponseDto.success(user.getId()))
                .orElse(UserResponseDto.fail(ErrCode.INVALID_CREDENTIALS));
    }
}