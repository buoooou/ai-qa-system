package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.AuthRequest;
import com.ai.qa.user.api.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        System.out.println("测试login");
        return new AuthResponse("token");
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        System.out.println("测试register");
        return new AuthResponse("register");
    }

    @GetMapping("/{userId}")
    public String getUserById(@PathVariable("userId") Long userId) {
        System.out.println("测试userid");
        return "userid:"+userId;
    }

    @GetMapping("/health")
    public String health() {
        return "User Service is running!";
    }
}
