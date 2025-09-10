package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repo.UserRepository;
import com.ai.qa.user.infrastructure.config.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("注册请求: {}", request.toString());

        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(savedUser.getUsername());

        // 构建响应
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setToken(token);

        return response;
    }

    public UserResponse login(LoginRequest request) {
        log.info("登录请求: {}", request.toString());

        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名错误");
        }

        User user = userOptional.get();

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());

        // 构建响应
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setToken(token);

        return response;
    }
}