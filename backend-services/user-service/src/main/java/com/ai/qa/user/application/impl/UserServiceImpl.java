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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UserInfoResponse register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        
        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}", savedUser.getUsername());
        
        return convertToUserInfoResponse(savedUser);
    }
    
    @Override
    public LoginResponse login(UserLoginRequest request) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成token（这里简化处理，实际项目中应该使用JWT）
        String token = generateToken(user.getId());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(convertToUserInfoResponse(user));
        
        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }
    
    @Override
    @Transactional
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("用户修改密码成功: {}", user.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public UserInfoResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        user.setNickname(request.getNickname());
        User savedUser = userRepository.save(user);
        
        log.info("用户修改昵称成功: {} -> {}", user.getUsername(), request.getNickname());
        return convertToUserInfoResponse(savedUser);
    }
    
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        return convertToUserInfoResponse(userOpt.get());
    }
    
    @Override
    public UserInfoResponse getUserInfoByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        return convertToUserInfoResponse(userOpt.get());
    }
    
    /**
     * 将User实体转换为UserInfoResponse
     */
    private UserInfoResponse convertToUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }
    
    /**
     * 生成token（简化实现，实际项目中应使用JWT）
     */
    private String generateToken(Long userId) {
        // 这里简化处理，实际项目中应该使用JWT生成token
        return "token_" + userId + "_" + System.currentTimeMillis();
    }
}
