package com.ai.qa.user.api.controller.impl;

import com.ai.qa.user.api.controller.UserController;
import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块控制器实现
 * 提供登录、注册等 RESTful 端点，作为 Web 层与业务层的调度入口
 *
 * @author Chen Guoping
 * @since 2025-09
 * @version 1.0
 */
@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录端点
     * 委托 UserService 完成身份认证，成功返回 200 OK
     *
     * @param loginRequest 用户名 + 密码
     * @return 登录结果（含 token 等信息）
     * @apiNote POST /api/users/login
     */
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        // 交由业务层处理登录逻辑
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 注册端点
     * 委托 UserService 创建账户，成功返回 201 Created
     *
     * @param registerRequest 注册表单
     * @return 注册结果（含用户 ID 等信息）
     * @apiNote POST /api/users/register
     */
    @Override
    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest) {
        // 交由业务层处理注册逻辑
        RegisterResponse response = userService.register(registerRequest);
        return ResponseEntity.status(201).body(response);
    }
}