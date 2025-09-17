package com.ai.qa.user.application.impl;

import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.application.UserApplicationService;
import com.ai.qa.user.application.command.UserLoginCommand;
import com.ai.qa.user.application.command.UserRegisterCommand;
import com.ai.qa.user.application.command.UserUpdateNickCommand;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.service.UserDomainService;
import com.ai.qa.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {
    
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public User register(UserRegisterCommand command) {
        String username = command.getUsername();
        String password = command.getPassword();
        
        log.info("用户注册: {}", username);
        
        // 验证用户名格式
        if (!userDomainService.isValidUsername(username)) {
            log.warn("用户注册失败 - 用户名格式无效: {}", username);
            throw new IllegalArgumentException("用户名格式无效，只能包含字母、数字和下划线，长度3-50个字符");
        }
        
        // 验证密码强度
        if (!userDomainService.isStrongPassword(password)) {
            log.warn("用户注册失败 - 密码强度不足: {}", username);
            throw new IllegalArgumentException("密码强度不足，必须包含大小写字母和数字，长度至少6个字符");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUserName(username)) {
            log.warn("用户注册失败 - 用户名已存在: {}", username);
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建新用户
        User user = User.create(username, passwordEncoder.encode(password));
        
        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}", username);
        
        return savedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(UserLoginCommand command) {
        String username = command.getUsername();
        String password = command.getPassword();
        
        log.info("用户登录: {}", username);
        
        // 查询用户
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.warn("用户登录失败 - 用户不存在: {}", username);
                    return new IllegalArgumentException("用户名或密码错误");
                });
        
        // 验证密码
        if (!userDomainService.validatePassword(user, password)) {
            log.warn("用户登录失败 - 密码错误: {}", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUserName());
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUserName());
        response.setNickName(user.getNickName());
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(86400L); // 24小时
        
        log.info("用户登录成功: {}", username);
        return response;
    }
    
    @Override
    @Transactional
    public void updateNickName(UserUpdateNickCommand command, Long currentUserId) {
        Long userId = command.getUserId();
        String newNickName = command.getNickName();
        
        log.info("更新用户昵称: userId={}, currentUserId={}", userId, currentUserId);
        
        // 验证用户只能修改自己的昵称
        if (!userId.equals(currentUserId)) {
            log.warn("用户尝试修改他人昵称: userId={}, currentUserId={}", userId, currentUserId);
            throw new IllegalArgumentException("只能修改自己的昵称");
        }
        
        // 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: {}", userId);
                    return new IllegalArgumentException("用户不存在");
                });
        
        // 更新昵称
        user.updateProfile(newNickName);
        userRepository.save(user);
        
        log.info("用户昵称更新成功: userId={}, newNickName={}", userId, newNickName);
    }
    
    @Override
    @Transactional
    public void generateUserVectorEmbedding(Long userId) {
        log.info("生成用户向量嵌入: userId={}", userId);
        
        // 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: {}", userId);
                    return new IllegalArgumentException("用户不存在");
                });
        
        // 生成向量嵌入
        userDomainService.generateVectorEmbedding(user);
        userRepository.save(user);
        
        log.info("用户向量嵌入生成成功: userId={}", userId);
    }
}