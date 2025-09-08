package com.ai.qa.user.application;

import com.ai.qa.user.dto.LoginRequest;
import com.ai.qa.user.dto.LoginResponse;
import com.ai.qa.user.dto.RegisterRequest;
import com.ai.qa.user.dto.UserLoginRequest;
import com.ai.qa.user.dto.UserResponse;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import com.ai.qa.user.common.CommonUtil;
import com.ai.qa.user.api.exception.ErrCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务实现类
 * 
 * 实现用户管理相关的业务逻辑：
 * 1. 用户注册 - 验证用户信息，创建新用户
 * 2. 用户登录 - 验证凭据，返回用户信息
 * 3. 用户信息管理 - 查询、更新用户信息
 * 4. 用户状态管理 - 启用、禁用用户
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     * 
     * @param request 注册请求信息
     * @return UserResponse 注册结果
     */
    @Override
    public UserResponse register(RegisterRequest request) {
        log.info("开始用户注册，用户名: {}, 邮箱: {}", request.getUsername(), request.getEmail());
        
        // 1. 参数验证
        validateRegisterRequest(request);
        
        // 2. 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.USERNAME_EXISTS));
        }
        
        // 3. 检查邮箱是否已存在
        if (existsByEmail(request.getEmail())) {
            log.warn("邮箱已存在: {}", request.getEmail());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.EMAIL_EXISTS));
        }
        
        // 4. 创建用户实体
        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(1); // 1-启用，0-禁用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 5. 保存用户
        try {
            User savedUser = userRepository.save(user);
            log.info("用户注册成功，用户ID: {}, 用户名: {}", savedUser.getId(), savedUser.getUserName());
            
            return convertToUserResponse(savedUser);
            
        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.REGISTER_FAILED));
        }
    }
    
    /**
     * 用户登录
     * 
     * @param request 登录请求信息
     * @return LoginResponse 登录结果
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("开始用户登录，用户名: {}", request.getUsername());
        
        // 1. 参数验证
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        
        // 2. 查找用户
        Optional<User> userOpt = userRepository.findByUserName(request.getUsername());
        if (!userOpt.isPresent()) {
            log.warn("用户不存在: {}", request.getUsername());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.USER_NOT_FOUND));
        }
        
        User user = userOpt.get();
        
        // 3. 检查用户状态
        if (user.getStatus() == 0) { // 0-禁用，1-启用
            log.warn("用户已被禁用: {}", request.getUsername());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.USER_DISABLED));
        }
        
        // 4. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("密码错误，用户名: {}", request.getUsername());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PASSWORD_ERROR));
        }
        
        // 5. 构建登录响应 - 使用JWT版本的LoginResponse
        LoginResponse response = new LoginResponse();
        response.setUser(user);
        response.setAccessToken("mock-jwt-token-" + user.getId()); // 简化实现，实际应该生成JWT
        response.setRefreshToken("mock-refresh-token-" + user.getId());
        response.setExpiresIn(7200L); // 2小时
        
        log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUserName());
        return response;
    }
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return UserResponse 用户信息
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.debug("根据用户ID获取用户信息: {}", userId);
        
        if (userId == null || userId <= 0) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户ID: {}", userId);
            return null;
        }
        
        return convertToUserResponse(userOpt.get());
    }
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return UserResponse 用户信息
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.debug("根据用户名获取用户信息: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户名: {}", username);
            return null;
        }
        
        return convertToUserResponse(userOpt.get());
    }
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param email 新邮箱
     * @return UserResponse 更新后的用户信息
     */
    @Override
    public UserResponse updateUserInfo(Long userId, String email) {
        log.info("更新用户信息，用户ID: {}, 新邮箱: {}", userId, email);
        
        // 1. 参数验证
        if (userId == null || userId <= 0) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        if (email == null || !CommonUtil.isValidEmail(email)) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.EMAIL_FORMAT_ERROR));
        }
        
        // 2. 查找用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户ID: {}", userId);
            return null;
        }
        
        User user = userOpt.get();
        
        // 3. 检查邮箱是否被其他用户使用
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        if (existingUserOpt.isPresent() && !existingUserOpt.get().getId().equals(userId)) {
            log.warn("邮箱已被其他用户使用: {}", email);
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.EMAIL_EXISTS));
        }
        
        // 4. 更新用户信息
        user.setEmail(email);
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            User savedUser = userRepository.save(user);
            log.info("用户信息更新成功，用户ID: {}", userId);
            return convertToUserResponse(savedUser);
            
        } catch (Exception e) {
            log.error("用户信息更新失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.UPDATE_USER_FAILED));
        }
    }
    
    /**
     * 修改用户密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return boolean 修改结果
     */
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改用户密码，用户ID: {}", userId);
        
        // 1. 参数验证
        if (userId == null || userId <= 0) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        if (newPassword == null || !CommonUtil.isValidPassword(newPassword)) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PASSWORD_FORMAT_ERROR));
        }
        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.SAME_PASSWORD_ERROR));
        }
        
        // 2. 查找用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户ID: {}", userId);
            return false;
        }
        
        User user = userOpt.get();
        
        // 3. 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("旧密码错误，用户ID: {}", userId);
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.OLD_PASSWORD_ERROR));
        }
        
        // 4. 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            userRepository.save(user);
            log.info("密码修改成功，用户ID: {}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("密码修改失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.CHANGE_PASSWORD_FAILED));
        }
    }
    
    /**
     * 禁用用户
     * 
     * @param userId 用户ID
     * @return boolean 操作结果
     */
    @Override
    public boolean disableUser(Long userId) {
        return changeUserStatus(userId, false);
    }
    
    /**
     * 启用用户
     * 
     * @param userId 用户ID
     * @return boolean 操作结果
     */
    @Override
    public boolean enableUser(Long userId) {
        return changeUserStatus(userId, true);
    }
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return boolean true-存在，false-不存在
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUserName(username);
    }
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return boolean true-存在，false-不存在
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 验证注册请求参数
     * 
     * @param request 注册请求
     */
    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() == null || !CommonUtil.isValidUsername(request.getUsername())) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.USERNAME_FORMAT_ERROR));
        }
        if (request.getPassword() == null || !CommonUtil.isValidPassword(request.getPassword())) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PASSWORD_FORMAT_ERROR));
        }
        if (request.getEmail() == null || !CommonUtil.isValidEmail(request.getEmail())) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.EMAIL_FORMAT_ERROR));
        }
        if (request.getConfirmPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PASSWORD_CONFIRM_ERROR));
        }
    }
    
    /**
     * 转换User实体为UserResponse
     * 
     * @param user 用户实体
     * @return UserResponse 用户响应对象
     */
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.fromUser(user);
    }
    
    /**
     * 修改用户状态
     * 
     * @param userId 用户ID
     * @param enabled 是否启用
     * @return boolean 操作结果
     */
    private boolean changeUserStatus(Long userId, boolean enabled) {
        String action = enabled ? "启用" : "禁用";
        log.info("{}用户，用户ID: {}", action, userId);
        
        // 1. 参数验证
        if (userId == null || userId <= 0) {
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        }
        
        // 2. 查找用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户ID: {}", userId);
            return false;
        }
        
        User user = userOpt.get();
        
        // 3. 更新状态
        user.setStatus(enabled ? 1 : 0); // 1-启用，0-禁用
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            userRepository.save(user);
            log.info("{}用户成功，用户ID: {}", action, userId);
            return true;
            
        } catch (Exception e) {
            log.error("{}用户失败，用户ID: {}, 错误: {}", action, userId, e.getMessage());
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.CHANGE_USER_STATUS_FAILED));
        }
    }
    
    /**
     * JWT用户登录
     * 
     * @param request JWT登录请求信息
     * @return User 用户实体
     */
    @Override
    @Transactional(readOnly = true)
    public User login(UserLoginRequest request) {
        log.info("JWT用户登录请求，用户名: {}", request.getUserName());
        
        try {
            // 根据用户名查找用户
            Optional<User> userOptional = userRepository.findByUserName(request.getUserName());
            if (!userOptional.isPresent()) {
                throw new RuntimeException("用户名或密码错误");
            }
            
            User user = userOptional.get();
            
            // 检查用户状态
            if (user.getStatus() == 0) {
                throw new RuntimeException("用户已被禁用");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("用户名或密码错误");
            }
            
            log.info("JWT用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUserName());
            return user;
            
        } catch (Exception e) {
            log.error("JWT用户登录失败，用户名: {}, 错误信息: {}", request.getUserName(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * 根据ID查找用户
     * 
     * @param userId 用户ID
     * @return Optional<User> 用户信息
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        log.debug("根据ID查找用户，用户ID: {}", userId);
        return userRepository.findById(userId);
    }
}
