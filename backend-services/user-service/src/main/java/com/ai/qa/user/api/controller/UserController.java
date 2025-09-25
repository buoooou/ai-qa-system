package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户相关的API接口")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录验证，返回JWT令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "1003", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Response<LoginResponse> login(
            @Parameter(description = "登录请求参数", required = true) @RequestBody UserLoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return Response.success(loginResponse);
    }

    @Operation(summary = "用户注册", description = "注册新用户账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "1002", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Response<UserResponse> register(
            @Parameter(description = "注册请求参数", required = true) @RequestBody UserRegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        return Response.success(userResponse);
    }

    @Operation(summary = "更新用户昵称", description = "根据用户ID更新用户昵称")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "1001", description = "用户不存在")
    })
    @PutMapping("/{userId}/nickname")
    public Response<UserResponse> updateNickname(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "昵称更新请求参数", required = true) @RequestBody UpdateNicknameRequest request) {
        UserResponse userResponse = userService.updateNickname(userId, request);
        return Response.success(userResponse);
    }

    @Operation(summary = "根据用户ID查询用户", description = "通过用户ID获取用户详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "1001", description = "用户不存在")
    })
    @GetMapping("/{userId}")
    public Response<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return Response.success(userResponse);
    }

    @Operation(summary = "根据用户名查询用户", description = "通过用户名获取用户详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "1001", description = "用户不存在")
    })
    @GetMapping("/username/{username}")
    public Response<UserResponse> getUserByUsername(
            @Parameter(description = "用户名", required = true) @PathVariable String username) {
        UserResponse userResponse = userService.getUserByUsername(username);
        return Response.success(userResponse);
    }
}
