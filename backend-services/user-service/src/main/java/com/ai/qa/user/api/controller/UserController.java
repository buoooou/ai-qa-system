package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserController", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    public Response<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO request) {
        UserResponseDTO userInfo = userService.register(request);
        return Response.success("注册成功", userInfo);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录获取 token")
    public Response<String> login(@Valid @RequestBody UserLoginDTO request) {
        String token = userService.login(request);
        return Response.success("登录成功", token);
    }

    @PutMapping("/{id}/nickname")
    @Operation(summary = "修改昵称", description = "更新用户昵称")
    public Response<UserResponseDTO> updateNickname(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNicknameDTO request) {
        UserResponseDTO userInfo = userService.updateNickname(id, request.getNickname());
        return Response.success("修改成功", userInfo);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "更新用户密码")
    public Response<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordDTO request) {
        userService.updatePassword(id, request.getOldPassword(), request.getNewPassword());
        return Response.success("修改成功", null);
    }
}
