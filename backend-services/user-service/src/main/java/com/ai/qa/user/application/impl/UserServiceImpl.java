package com.ai.qa.user.application.impl;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.service.UserDomainService;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户应用服务实现 - 只做协调工作
 * 业务逻辑已经移到Domain层，这里只负责：
 * 1. 事务管理
 * 2. 调用领域服务
 * 3. 数据转换
 * 4. 异常处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    
    @Override
    @Transactional
    public UserInfoResponse register(UserRegisterRequest request) {
        try {
            // 调用领域服务处理业务逻辑
            User user = userDomainService.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                () -> userRepository.existsByUsername(request.getUsername())
            );
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            log.info("用户注册成功: {}", savedUser.getUsername());
            return convertToUserInfoResponse(savedUser);
            
        } catch (IllegalArgumentException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage(), e);
            throw new RuntimeException("注册失败，请稍后重试");
        }
    }
    
    @Override
    public LoginResponse login(UserLoginRequest request) {
        try {
            // 调用领域服务处理业务逻辑
            User user = userDomainService.authenticateUser(
                request.getUsername(),
                request.getPassword(),
                username -> userRepository.findByUsername(username).orElse(null)
            );
            
            // 生成token（这里简化处理，实际项目中应该使用JWT）
            String token = generateToken(user.getId());
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUserInfo(convertToUserInfoResponse(user));
            
            log.info("用户登录成功: {}", user.getUsername());
            return response;
            
        } catch (IllegalArgumentException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage(), e);
            throw new RuntimeException("登录失败，请稍后重试");
        }
    }
    
    @Override
    @Transactional
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        try {
            // 查找用户
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("用户不存在");
            }
            
            User user = userOpt.get();
            
            // 调用领域服务处理业务逻辑
            userDomainService.changeUserPassword(user, request.getOldPassword(), request.getNewPassword());
            
            // 保存用户
            userRepository.save(user);
            
            log.info("用户修改密码成功: {}", user.getUsername());
            return true;
            
        } catch (IllegalArgumentException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage(), e);
            throw new RuntimeException("修改密码失败，请稍后重试");
        }
    }
    
    @Override
    @Transactional
    public UserInfoResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        try {
            // 查找用户
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("用户不存在");
            }
            
            User user = userOpt.get();
            
            // 调用领域服务处理业务逻辑
            userDomainService.updateUserNickname(user, request.getNickname());
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            log.info("用户修改昵称成功: {} -> {}", user.getUsername(), request.getNickname());
            return convertToUserInfoResponse(savedUser);
            
        } catch (IllegalArgumentException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("修改昵称失败: {}", e.getMessage(), e);
            throw new RuntimeException("修改昵称失败，请稍后重试");
        }
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
    
    private String generateToken(String userId) {
        return "token_" + userId + "_" + System.currentTimeMillis();
    }
}
