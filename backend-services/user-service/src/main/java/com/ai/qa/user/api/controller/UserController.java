package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserLoginRequest request) {
        log.info("登录请求: {}", request.getUsername());
        return userService.login(request);
    }

    @PostMapping("/register")
    public UserInfoResponse register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("注册请求: {}", request.getUsername());
        return userService.register(request);
    }

    @GetMapping("/{userId}")
    public UserInfoResponse getUserById(@PathVariable("userId") Long userId) {
        log.info("获取用户信息请求: {}", userId);
        return userService.getUserInfo(userId);
    }

    @GetMapping("/username/{username}")
    public UserInfoResponse getUserByUsername(@PathVariable("username") String username) {
        log.info("根据用户名获取用户信息请求: {}", username);
        return userService.getUserInfoByUsername(username);
    }

    @GetMapping("/health")
    public String health() {
        return "User Service is running!";
    }
}
