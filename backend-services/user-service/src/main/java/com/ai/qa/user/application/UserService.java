package com.ai.qa.user.application;

import com.ai.qa.user.dto.*;
import com.ai.qa.user.entity.User;

import java.util.Optional;

/**
 * 用户服务接口
 *
 * 定义用户管理相关的业务操作：
 * 1. 用户注册
 * 2. 用户登录
 * 3. 用户信息管理
 * 4. 用户状态管理
 *
 * @author David
 * @version 1.0
 * @since 2025-09-06
 */
public interface UserService {

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
    User login(UserLoginRequest request);

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
    boolean disableUser(Long userId);

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return boolean 操作结果
     */
    boolean enableUser(Long userId);

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
    boolean existsByEmail(String email);
}