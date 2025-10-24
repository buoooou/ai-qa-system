package com.ai.qa.user.application.service;


import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserLoginResponse;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.domain.model.User;


@Service
public interface UserService {

    UserResponse updateNickname(String username, String newNickname);

    /**
     * 用户注册
     * 
     * @param request 注册请求信息
     * @return UserResponse 注册结果
     */
    UserResponse register(RegisterRequest request);
    
    /**
     * 用户登录
     * 
     * @param request 登录请求信息
     * @return LoginResponse 登录结果
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户登录（JWT版本）
     * 
     * @param request JWT登录请求信息
     * @return User 用户实体
     */
    UserLoginResponse login(UserLoginRequest request);
    
    /**
     * 根据ID查找用户
     * 
     * @param userId 用户ID
     * @return Optional<User> 用户信息
     */
    Optional<User> findById(Long userId);
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return UserResponse 用户信息
     */
    UserResponse getUserById(Long userId);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return UserResponse 用户信息
     */
    UserResponse getUserByUsername(String username);
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param email 新邮箱
     * @return UserResponse 更新后的用户信息
     */
    UserResponse updateUserInfo(Long userId, String email);
    
    /**
     * 修改用户密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return boolean 修改结果
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 禁用用户
     * 
     * @param userId 用户ID
     * @return boolean 操作结果
     */
    // boolean disableUser(Long userId);
    
    /**
     * 启用用户
     * 
     * @param userId 用户ID
     * @return boolean 操作结果
     */
    // boolean enableUser(Long userId);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return boolean true-存在，false-不存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return boolean true-存在，false-不存在
     */
    // boolean existsByEmail(String email);
}
