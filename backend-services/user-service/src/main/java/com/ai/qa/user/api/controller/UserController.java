package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户注册、登录、信息管理相关API")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "参数错误或用户名已存在")
    })
    public Response<UserInfoResponse> register(
            @Parameter(description = "注册信息", required = true) 
            @Valid @RequestBody UserRegisterRequest request) {
        try {
            UserInfoResponse userInfo = userService.register(request);
            return Response.success("注册成功", userInfo);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Response.error(400, e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录认证")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public Response<LoginResponse> login(
            @Parameter(description = "登录信息", required = true) 
            @Valid @RequestBody UserLoginRequest request) {
        try {
            LoginResponse loginResponse = userService.login(request);
            return Response.success("登录成功", loginResponse);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return Response.error(401, e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改用户密码")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "密码修改成功"),
        @ApiResponse(responseCode = "400", description = "原密码错误或参数错误")
    })
    public Response<Boolean> changePassword(
            @Parameter(description = "用户ID", required = true) 
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "密码修改信息", required = true) 
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            boolean success = userService.changePassword(userId, request);
            return Response.success("密码修改成功", success);
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            return Response.error(400, e.getMessage());
        }
    }
    
    /**
     * 修改昵称
     */
    @PutMapping("/nickname")
    @Operation(summary = "修改昵称", description = "修改用户昵称")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "昵称修改成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public Response<UserInfoResponse> updateNickname(
            @Parameter(description = "用户ID", required = true) 
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "昵称修改信息", required = true) 
            @Valid @RequestBody UpdateNicknameRequest request) {
        try {
            UserInfoResponse userInfo = userService.updateNickname(userId, request);
            return Response.success("昵称修改成功", userInfo);
        } catch (Exception e) {
            log.error("修改昵称失败: {}", e.getMessage());
            return Response.error(400, e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Response<UserInfoResponse> getUserInfo(
            @Parameter(description = "用户ID", required = true) 
            @RequestHeader("X-User-Id") Long userId) {
        try {
            UserInfoResponse userInfo = userService.getUserInfo(userId);
            return Response.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Response.error(404, e.getMessage());
        }
    }
    
    /**
     * 根据用户名获取用户信息（管理员接口）
     */
    @GetMapping("/info/{username}")
    @Operation(summary = "根据用户名查询用户", description = "管理员接口，根据用户名查询用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Response<UserInfoResponse> getUserInfoByUsername(
            @Parameter(description = "用户名", required = true) 
            @PathVariable String username) {
        try {
            UserInfoResponse userInfo = userService.getUserInfoByUsername(username);
            return Response.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Response.error(404, e.getMessage());
        }
    }
}
