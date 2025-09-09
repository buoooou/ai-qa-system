package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户模块 API 接口定义
 * 采用“接口-实现”分离模式，方便后续扩展与维护
 *
 * @author Chen Guoping
 * @since 2025-09
 * @version 1.0
 */
@Tag(name = "用户管理", description = "登录、注册等身份认证相关端点")
@RequestMapping("/api/users")
public interface UserController {

    /**
     * 登录
     *
     * @param loginRequest 用户名+密码
     * @return 登录结果（含 token）
     */
    @Operation(summary = "用户登录", description = "校验账号密码，颁发访问令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "账号或密码错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @Parameter(description = "登录请求", required = true)
            @RequestBody LoginRequest loginRequest
    );

    /**
     * 注册
     *
     * @param registerRequest 注册表单
     * @return 注册结果（含用户 ID）
     */
    @Operation(summary = "用户注册", description = "创建新账号，用户名全局唯一")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(
            @Parameter(description = "注册请求", required = true)
            @RequestBody RegisterRequest registerRequest
    );
}