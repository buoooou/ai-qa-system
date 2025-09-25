package com.ai.qa.user.api.controller;
import com.ai.qa.user.domain.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;


import com.ai.qa.user.api.dto.ApiResponse;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.api.dto.UserRequestDto;
import com.ai.qa.user.application.service.JwtService;
import com.ai.qa.user.application.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * 用户控制层
 *
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    
    private final UserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody UserRequestDto request){
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        User user = userService.login(request);
        AuthResponse response= new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId().toString());
        return ResponseEntity.ok(ApiResponse.success("登录成功,token获取", response));

    
    }

    /**
     * 用户注册接口
     * @param user
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRequestDto userRequestDto) {
        // 调用服务层执行注册逻辑
        return ResponseEntity.ok(ApiResponse.success("用户注册成功", userService.register(userRequestDto)));
    }

    /**
     * 用户昵称更新
     * @param user
     * @return
     */
    @PostMapping("/updateNickName")
    public ResponseEntity<ApiResponse<?>> updateNickname(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("用户昵称更新成功", userService.updateNickName(userRequestDto)));
    }

    /**
     * 密码更新
     * @param user
     * @return
     */
    @PostMapping("/updatePassword")
    public ResponseEntity<ApiResponse<?>> updatePassword(@RequestBody UserRequestDto userRequestDto) {
    	userService.updatePassword(userRequestDto);
        return ResponseEntity.ok(ApiResponse.success("密码更新成功", null));
    }
    /**
     * 健康检查接口
     * 
     * 用于检查用户服务是否正常运行
     * 
     * @return Response<String> 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running\", \"User Service is running");
    }
}
