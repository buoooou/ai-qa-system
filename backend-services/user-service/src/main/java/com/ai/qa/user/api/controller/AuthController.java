// com/ai/qa/user/api/controller/AuthController.java
package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.JwtResponse;
import com.ai.qa.user.api.dto.response.MessageResponse;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.JwtUtils;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repo.UserRepository;
import com.ai.qa.user.application.service.impl.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录和注册接口")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService; // 依赖应用层
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/signin")
    @Operation(summary = "用户登录", description = "用户登录并获取JWT令牌")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String jwt = jwtUtils.generateJwtToken(userDetails);
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getUsername()));
    }

    @PostMapping("/signup")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) { // 调用应用层
            return ResponseEntity.badRequest().body(new MessageResponse("Error: 用户名已存在!"));
        }
        userService.registerUser(request); // 调用应用层
        return ResponseEntity.ok(new MessageResponse("用户注册成功!"));
    }
}