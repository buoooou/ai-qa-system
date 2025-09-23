package com.ai.qa.user.application.impl;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserInfoResponse register(UserRegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = User.createNewUser(request.getUsername(), request.getPassword(), request.getNickname(), passwordEncoder);
        user = userRepository.save(user);
        
        log.info("用户注册成功: {}", user.getUsername());
        
        return convertToUserInfoResponse(user);
    }

    @Override
    public LoginResponse login(UserLoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();
        
        // 验证密码
        if (!user.verifyPassword(request.getPassword(), passwordEncoder)) {
            throw new RuntimeException("用户名或密码错误");
        }

        log.info("用户登录成功: {}", user.getUsername());
        
        // 生成简单的 token（实际项目中应该使用 JWT）
        String token = generateSimpleToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(convertToUserInfoResponse(user));
        
        return response;
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        log.info("用户修改密码请求: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        user.changePassword(request.getOldPassword(), request.getNewPassword(), passwordEncoder);
        userRepository.save(user);
        
        log.info("用户密码修改成功: {}", userId);
        return true;
    }

    @Override
    @Transactional
    public UserInfoResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        log.info("用户修改昵称请求: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        user.updateNickname(request.getNickname());
        userRepository.save(user);
        
        log.info("用户昵称修改成功: {}", userId);
        return convertToUserInfoResponse(user);
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        log.info("获取用户信息请求: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        return convertToUserInfoResponse(userOpt.get());
    }

    @Override
    public UserInfoResponse getUserInfoByUsername(String username) {
        log.info("根据用户名获取用户信息请求: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        return convertToUserInfoResponse(userOpt.get());
    }

    /**
     * 转换为用户信息响应
     */
    private UserInfoResponse convertToUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }

    /**
     * 生成简单的 token
     * 实际项目中应该使用 JWT
     */
    private String generateSimpleToken(User user) {
        // 简单的 token 生成策略：用户ID + 时间戳 + 随机字符串
        long timestamp = System.currentTimeMillis();
        return String.format("token_%d_%d_%s", user.getId(), timestamp, 
                Integer.toHexString(user.getUsername().hashCode()));
    }
}