package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.service.UserService;
import com.ai.qa.user.infrastructure.adapter.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserApplicationService(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 注册用户
    @Transactional
    public UserResponseDto<AuthResponse> register(RegisterReqDto request) {
        // 调用领域层逻辑
        User user = userService.registerUser(request);
        // 生成 JWT Token
        String token = jwtTokenProvider.createToken(user.getUsername());
        return UserResponseDto.success(new AuthResponse(token));
    }

    // 用户登录
    @Transactional
    public UserResponseDto<AuthResponse> login(LoginReqDto request) {
        // 调用领域层逻辑
        User user = userService.loginUser(request);
        // 生成 JWT Token
        String token = jwtTokenProvider.createToken(user.getUsername());
        return UserResponseDto.success(new AuthResponse(token));
    }

    // 更新用户昵称
    @Transactional
    public UserResponseDto<AuthResponse> updateNickname(UpdateNicknameReqDto request) {
        userService.updateNickname(request);
        String token = jwtTokenProvider.createToken(request.getUsername());
        return UserResponseDto.success(new AuthResponse(token));
    }

}