package com.ai.qa.user.application.impl;

import com.ai.qa.user.api.dto.AuthRequest;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.application.userService;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.common.TokenStoreService;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements userService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TokenStoreService tokenStoreService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, TokenStoreService tokenStoreService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.tokenStoreService = tokenStoreService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        // 检查用户名和密码是否为空
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        // 根据用户名查找用户
        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
        if (!userOptional.isPresent()) {
            throw new NoSuchElementException("用户不存在");
        }

        User user = userOptional.get();

        // 验证密码
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new IllegalStateException("用户已禁用");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());

        // 返回令牌
        return new AuthResponse(token);
    }

    @Override
    public User register(String username, String password) {
        // 检查用户名和密码是否为空
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        // 密码加密存储
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(username); // 默认使用用户名作为昵称

        // 保存用户
        return userRepository.save(user);
    }

    @Override
    public boolean logout(String token) {
        try {
            // 使令牌失效
            jwtUtil.invalidateToken(token);
            return true;
        } catch (Exception e) {
            // 令牌无效或已过期
            return false;
        }
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
    }
}