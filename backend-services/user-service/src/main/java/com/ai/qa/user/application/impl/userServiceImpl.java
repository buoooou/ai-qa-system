package com.ai.qa.user.application.impl;

import com.ai.qa.user.application.userService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserRegisterRequest;
import com.ai.qa.user.api.dto.UpdateNickRequest;
import com.ai.qa.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class userServiceImpl implements userService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     * @param request 注册请求DTO，包含用户名和密码
     * @return 注册用户实体
     * @throws IllegalArgumentException 如果参数无效
     * @throws RuntimeException 如果用户名已存在
     */
    @Override
    @Transactional
    public User register(UserRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        log.info("用户注册: {}", username);
        
        // 检查用户名是否已存在
        if (userRepository.existsByUserName(username)) {
            log.warn("用户注册失败 - 用户名已存在: {}", username);
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        
        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}", username);
        return savedUser;
    }

    /**
     * 用户登录
     * @param request 登录请求DTO，包含用户名和密码
     * @return 登录响应DTO，包含JWT Token和用户信息
     * @throws IllegalArgumentException 如果参数无效
     * @throws RuntimeException 如果用户名或密码错误
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(UserLoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        log.info("用户登录: {}", username);
        
        // 查询用户
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.warn("用户登录失败 - 用户不存在: {}", username);
                    return new IllegalArgumentException("用户名或密码错误");
                });
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
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

    /**
     * 更新用户昵称
     * @param currentUserId 当前登录用户ID（从JWT Token中获取，用于权限校验）
     * @param request 更新昵称请求DTO，包含用户ID和新昵称
     * @throws IllegalArgumentException 如果用户ID不匹配或用户不存在
     */
    @Override
    @Transactional
    public void updateNickName(Long currentUserId, UpdateNickRequest request) {
        Long userId = request.getUserId();
        String newNickName = request.getNickName();
        
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
        
        // 更新昵称，更新时间由@PreUpdate自动处理
        user.setNickName(newNickName);
        userRepository.save(user);
        
        log.info("用户昵称更新成功: userId={}, newNickName={}", userId, newNickName);
    }
}