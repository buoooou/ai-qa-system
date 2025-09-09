package com.ai.qa.user.application.service.impl;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务实现
 * 处理登录、注册等业务逻辑
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     * 验证用户名密码并生成令牌
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     * @throws BusinessException 用户不存在或密码错误时抛出
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (!userOpt.isPresent()) {
            throw BusinessException.userNotFound();
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw BusinessException.passwordIncorrect();
        }

        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_SUCCESS);
        response.setErrorCode(ErrCode.SUCCESS);
        response.setToken(jwtUtil.generateToken(user.getUsername()));
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setLoginTime(LocalDateTime.now());

        return response;
    }

    /**
     * 用户注册
     * 创建新账户，校验用户名唯一性与密码一致性
     *
     * @param registerRequest 注册请求
     * @return 注册响应
     * @throws BusinessException 用户名已存在或密码不一致时抛出
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw BusinessException.userAlreadyExists();
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw BusinessException.passwordMismatch();
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_CREATED);
        response.setErrorCode(ErrCode.CREATED);
        response.setUserId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setNickname(savedUser.getNickname());
        response.setRegisterTime(LocalDateTime.now());

        return response;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}