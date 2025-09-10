package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UserResponseDto;
import com.ai.qa.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody RegisterReqDto request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody LoginReqDto request) {
        return userService.login(request);
    }
}
