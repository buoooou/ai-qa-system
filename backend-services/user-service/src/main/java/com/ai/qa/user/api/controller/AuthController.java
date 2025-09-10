package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.JwtResponse;
import com.ai.qa.user.api.dto.response.MessageResponse;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repo.UserRepository;
import com.ai.qa.user.application.service.UserDetailsServiceImpl;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.common.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          UserService userService,
                          JwtUtils jwtUtils,
                          UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signin")
    @Operation(summary = "用户登录", description = "用户登录并获取JWT令牌")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 验证用户凭据
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 将认证信息存入安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        //使用 userDetails 作为 generateJwtToken 的参数更合理，它在语义匹配、逻辑明确性和设计原则上都更优。
        String jwt = jwtUtils.generateJwtToken(userDetails);
        
        // 获取用户角色
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(item -> item.getAuthority())
//                .collect(Collectors.toList());

        // 获取完整用户信息
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

        // 返回JWT响应
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername()
        ));
    }

    @PostMapping("/signup")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        // 创建新用户
        userService.registerUser(registerRequest);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
