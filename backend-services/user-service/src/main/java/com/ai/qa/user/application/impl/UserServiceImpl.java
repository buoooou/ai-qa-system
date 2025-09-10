package com.ai.qa.user.application.impl;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import com.ai.qa.user.infrastructure.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrCode.INVALID_USERNAME_OR_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrCode.INVALID_USERNAME_OR_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getUserName(), user.getId());
        UserResponse userResponse = convertToUserResponse(user);
        
        return new LoginResponse(token, jwtExpiration, userResponse);
    }

    @Override
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new BusinessException(ErrCode.USERNAME_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUserName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());

        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    @Override
    public UserResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrCode.USER_NOT_FOUND));
        
        user.setNickname(request.getNickname());
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrCode.USER_NOT_FOUND));
        return convertToUserResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new BusinessException(ErrCode.USER_NOT_FOUND));
        return convertToUserResponse(user);
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}