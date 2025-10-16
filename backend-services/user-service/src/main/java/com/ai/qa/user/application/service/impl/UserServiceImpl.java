package com.ai.qa.user.application.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserLoginResponse;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.api.exception.ErrorCode;
import com.ai.qa.user.api.exception.UserServiceException;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.Constants;
import com.ai.qa.user.domain.mapper.UserMapper;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repository.UserRepo;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return UserResponse 用户信息
     */
    @Operation(summary = "根据用户名取得用户详细信息。")
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.debug("根据用户名获取用户信息: {}", username);

        return userRepository.findByUsername(username)
                .map(x -> userMapper.toUserDTO(x))
                .orElseThrow(() -> new EntityNotFoundException());
    }

    @Operation(summary = "更新该用户的昵称。")
    @Transactional
    @Override
    public UserResponse updateNickname(String username, String newNickname) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException());

        user.changeNickname(newNickname);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    @Operation(summary = "注册新用户。")
    @Transactional
    @Override
    public UserResponse register(RegisterRequest request) {

        log.info("开始用户注册，用户名: {}", request.getUsername());

        // 参数验证
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname()).build();
        newUser.registerValidation(request.getConfirmPassword());

        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new UserServiceException(ErrorCode.USER_ALREADY_EXIST);
        }

        // 保存用户
        try {
            User savedUser = userRepository.save(newUser);
            log.info("用户注册成功，用户ID: {}, 用户名: {}", savedUser.getId(), savedUser.getUsername());

            return userMapper.toUserDTO(savedUser);

        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException(Constants.MSG_USER_REGISTER_FAIL);
        }
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return boolean true-存在，false-不存在
     */
    @Operation(summary = "检查用户名是否存在。")
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求信息
     * @return LoginResponse 登录结果
     */
    @Operation(summary = "用户登录。")
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("开始用户登录，用户名: {}", request.getUsername());

        // 参数验证
        User validateUser = User.builder().username(request.getUsername()).password(request.getPassword()).build();
        validateUser.loginValidation();

        // 根据用户名查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException());

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("密码错误，用户名: {}", request.getUsername());
            throw new UserServiceException(ErrorCode.UNAUTHORIZED);
        }

        // 5. 构建登录响应 - 使用JWT版本的LoginResponse
        LoginResponse response = new LoginResponse();
        LoginResponse.User loginUser = response.new User();
        loginUser.setId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickname(user.getNickname());
        loginUser.setCreateTime(user.getCreateTime());
        loginUser.setUpdateTime(user.getUpdateTime());
        response.setUser(loginUser);
        response.setAccessToken("mock-jwt-token-" + user.getId()); // 简化实现，实际应该生成JWT
        response.setRefreshToken("mock-refresh-token-" + user.getId());
        response.setExpiresIn(7200L); // 2小时

        log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
        return response;
    }

    /**
     * JWT用户登录
     *
     * @param request JWT登录请求信息
     * @return User 用户实体
     */
    @Operation(summary = "JWT用户登录。")
    @Override
    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        log.info("JWT用户登录请求，用户名: {}", request.getUsername());

        try {
            // 根据用户名查找用户
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException());

            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("密码错误，用户名: {}", request.getUsername());
                throw new UserServiceException(ErrorCode.UNAUTHORIZED);
            }

            log.info("JWT用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            return UserLoginResponse.builder().id(user.getId()).username(user.getUsername()).password(user.getPassword())
                .nickname(user.getNickname()).createTime(user.getCreateTime()).updateTime(user.getUpdateTime()).build();

        } catch (Exception e) {
            log.error("JWT用户登录失败，用户名: {}, 错误信息: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return UserResponse 用户信息
     */
    @Operation(summary = "根据用户ID取得该用户详细信息。")
    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(Long userId) {
        log.debug("根据用户ID获取用户信息: {}", userId);

        return userRepository.findById(userId)
                .map(x -> userMapper.toUserDTO(x))
                .orElseThrow(() -> new EntityNotFoundException());
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

    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param email 新邮箱
     * @return UserResponse 更新后的用户信息
     */
    @Override
    @Transactional
    public UserResponse updateUserInfo(Long userId, String email) {
        log.info("更新用户信息，用户ID: {}, 新邮箱: {}", userId, email);
        
        // 1. 参数验证
        // if (userId == null || userId <= 0) {
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        // }
        // if (email == null || !CommonUtil.isValidEmail(email)) {
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.EMAIL_FORMAT_ERROR));
        // }
        
        // 2. 查找用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户ID: {}", userId);
            return null;
        }
        
        User user = userOpt.get();
        
        // 3. 检查邮箱是否被其他用户使用
        // Optional<User> existingUserOpt = userRepository.findByEmail(email);
        // if (existingUserOpt.isPresent() && !existingUserOpt.get().getId().equals(userId)) {
        //     log.warn("邮箱已被其他用户使用: {}", email);
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.EMAIL_EXISTS));
        // }
        
        // 4. 更新用户信息
        // user.setEmail(email);
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            User savedUser = userRepository.save(user);
            log.info("用户信息更新成功，用户ID: {}", userId);
            return userMapper.toUserDTO(savedUser);
            
        } catch (Exception e) {
            log.error("用户信息更新失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            throw new UserServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
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
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改用户密码，用户ID: {}", userId);
        
        // 参数验证
        // if (userId == null || userId <= 0) {
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        // }
        // if (oldPassword == null || oldPassword.trim().isEmpty()) {
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PARAM_ERROR));
        // }
        // if (newPassword == null || !CommonUtil.isValidPassword(newPassword)) {
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.PASSWORD_FORMAT_ERROR));
        // }
        // if (oldPassword.equals(newPassword)) {
        //     throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.SAME_PASSWORD_ERROR));
        // }
        
        // 查找用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            log.warn("用户不存在，用户ID: {}", userId);
            return false;
        }

        User user = userOpt.get();
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("旧密码错误，用户ID: {}", userId);
            throw new UserServiceException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            userRepository.save(user);
            log.info("密码修改成功，用户ID: {}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("密码修改失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            throw new UserServiceException(ErrorCode.CHANGE_PASSWORD_FAIL);
        }
    }

}
