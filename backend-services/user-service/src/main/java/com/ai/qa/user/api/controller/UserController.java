package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.ApiResponse;
import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.application.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        log.info("Registering user with username: {}", request.getUsername());

        UserResponse response = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest request) {
        log.info("Logging in user with username: {}", request.getUsername());

        UserResponse response = userService.login(request);
        log.info("login ok, username: {}", response.getUsername());
        log.info("login ok, ResponseEntity: {}", ResponseEntity.ok(ApiResponse.success(response).toString()));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/getUserName")
    public ResponseEntity<ApiResponse<String>> getUserName(@RequestParam("userId") Long userId) {
        log.info("Getting user with userid: {}", userId);
        String username = userService.getUserName(userId);
        log.info("User with username: {}", username);
        return ResponseEntity.ok(ApiResponse.success(username));
    }
}