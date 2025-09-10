package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UpdateNicknameReqDto;
import com.ai.qa.user.api.dto.UserResponseDto;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.security.CurrentUser;
import com.ai.qa.user.infrastructure.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor // 使用构造注入
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public UserResponseDto<Long> register(@RequestBody @Valid RegisterReqDto request) {
        return userService.register(request);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public UserResponseDto<Map<String, String>> login(
            @RequestBody @Valid LoginReqDto request,
            BindingResult result) {

        // 参数校验失败
        if (result.hasErrors()) {
            return UserResponseDto.fail(ErrCode.INVALID_PARAMS);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            Map<String, String> data = Collections.singletonMap("token", token);
            return UserResponseDto.success(data);

        } catch (AuthenticationException e) {
            return UserResponseDto.fail(ErrCode.INVALID_CREDENTIALS);
        }
    }
    /**
     * 更新当前用户昵称
     */
    @PutMapping("/nickname")
    public UserResponseDto<String> updateNickname(
            @Validated @RequestBody UpdateNicknameReqDto request,
            @CurrentUser User currentUser) {

        return userService.updateNickname(currentUser.getId(), request.getNickname());
    }

}