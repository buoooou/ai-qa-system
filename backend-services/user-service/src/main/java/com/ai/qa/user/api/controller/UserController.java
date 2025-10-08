package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.application.dto.UpdateNicknameRequest;
import com.ai.qa.user.application.service.UserApplicationService;
import com.ai.qa.user.domain.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserController", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    public Response<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO request) {
        UserResponseDTO userInfo = userService.register(request);
        return Response.success("注册成功", userInfo);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录获取 token")
    public Response<LoginResponseDTO> login(@Valid @RequestBody UserLoginDTO request) {
        LoginResponseDTO loginData = userService.login(request);
        return Response.success("登录成功", loginData);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "更新用户密码")
    public Response<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordDTO request) {
        userService.updatePassword(id, request.getOldPassword(), request.getNewPassword());
        return Response.success("修改成功", null);
    }

    /**
     * 更新用户昵称的API端点
     *
     * @param userId  从URL路径中获取的用户ID
     * @param request 包含新昵称的请求体
     * @return 返回更新后的用户信息和HTTP状态码200 (OK)
     */
    @PutMapping("/{id}/nickname")
    @Operation(summary = "修改昵称", description = "更新用户昵称")
    public Response<UserResponseDTO> updateNickname(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNicknameDTO request) {
        // 校验。。。

        // 控制器只负责调用应用层，不处理业务逻辑
        UserResponseDTO userInfo = userService.updateNickname(id, request.getNickname());
        return Response.success("修改成功", userInfo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public Response<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO userInfo = userService.getUserById(id);
        return Response.success("获取成功", userInfo);
    }

    @GetMapping("/me")
    public Response<UserResponseDTO> getCurrentUser(@RequestHeader("x-user-id") Long userId) {
        if (userId == null) {
            return Response.error("缺少用户ID");
        }
        UserResponseDTO userInfo = userService.getUserById(userId);
        return Response.success("获取成功", userInfo);
    }

}
