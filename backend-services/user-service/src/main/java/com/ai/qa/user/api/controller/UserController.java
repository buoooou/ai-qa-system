package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.service.UserApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto<AuthResponse>> register(@RequestBody RegisterReqDto request) {
        return ResponseEntity.ok(userApplicationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto<AuthResponse>> login(@RequestBody LoginReqDto request) {
        return ResponseEntity.ok(userApplicationService.login(request));
    }

    @PutMapping("/nickname")
    public ResponseEntity<UserResponseDto<AuthResponse>> updateNickname(@RequestBody UpdateNicknameReqDto request) {
        return ResponseEntity.ok(userApplicationService.updateNickname(request));
    }
}