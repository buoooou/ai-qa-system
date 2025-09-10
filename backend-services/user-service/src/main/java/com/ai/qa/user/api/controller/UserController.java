package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户管理", description = "用户相关的API接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录", notes = "通过用户名和密码进行登录验证，返回JWT令牌")
    @ApiResponses({
        @ApiResponse(code = 200, message = "登录成功"),
        @ApiResponse(code = 1003, message = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Response<LoginResponse> login(
            @ApiParam(value = "登录请求参数", required = true) @RequestBody UserLoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return Response.success(loginResponse);
    }

    @ApiOperation(value = "用户注册", notes = "注册新用户账号")
    @ApiResponses({
        @ApiResponse(code = 200, message = "注册成功"),
        @ApiResponse(code = 1002, message = "用户名已存在")
    })
    @PostMapping("/register")
    public Response<UserResponse> register(
            @ApiParam(value = "注册请求参数", required = true) @RequestBody UserRegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        return Response.success(userResponse);
    }

    @ApiOperation(value = "更新用户昵称", notes = "根据用户ID更新用户昵称")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 1001, message = "用户不存在")
    })
    @PutMapping("/{userId}/nickname")
    public Response<UserResponse> updateNickname(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "昵称更新请求参数", required = true) @RequestBody UpdateNicknameRequest request) {
        UserResponse userResponse = userService.updateNickname(userId, request);
        return Response.success(userResponse);
    }

    @ApiOperation(value = "根据用户ID查询用户", notes = "通过用户ID获取用户详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 1001, message = "用户不存在")
    })
    @GetMapping("/{userId}")
    public Response<UserResponse> getUserById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return Response.success(userResponse);
    }

    @ApiOperation(value = "根据用户名查询用户", notes = "通过用户名获取用户详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 1001, message = "用户不存在")
    })
    @GetMapping("/username/{username}")
    public Response<UserResponse> getUserByUsername(
            @ApiParam(value = "用户名", required = true) @PathVariable String username) {
        UserResponse userResponse = userService.getUserByUsername(username);
        return Response.success(userResponse);
    }
}
