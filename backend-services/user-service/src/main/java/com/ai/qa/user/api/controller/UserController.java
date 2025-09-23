package com.ai.qa.user.api.controller;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UpdateNickRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.dto.LoginDto;
import com.ai.qa.user.application.dto.Response;
import com.ai.qa.user.application.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User API", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码验证用户身份")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名或密码为空")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户不存在或密码错误")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    public Response<?> login(@RequestBody LoginRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            if(username == null || password == null){
                return Response.error(ErrCode.BAD_REQUEST.getCode(), "用户名、密码不能为空");
            }

            LoginDto dto = userService.login(username, password);
            if(dto != null ){
                return Response.success(dto);
            } else {
                return Response.error(ErrCode.UNAUTHORIZED.getCode(), ErrCode.UNAUTHORIZED.getMessage());
            }
        } catch (UsernameNotFoundException e) {
            return Response.error(ErrCode.UNAUTHORIZED.getCode(), "用户不存在");
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    @PostMapping("/auth/register")
    @Operation(summary = "用户注册", description = "注册新用户并返回注册结果")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名、密码或昵称为空，或用户名已存在")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    public Response<?> register(@RequestBody RegisterRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String nickname = request.getNickname();
            if(username == null || password == null || nickname == null){
                return Response.error(ErrCode.BAD_REQUEST.getCode(), "用户名、密码、昵称不能为空");
            }
            if(userService.findByUsername(username) != null){
                return Response.error(ErrCode.BAD_REQUEST.getCode(), "用户名已存在");
            }
            return Response.success(userService.register(username, password, nickname));
        } catch (RuntimeException e) {
            return Response.error(ErrCode.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "用户修改昵称", description = "修改用户昵称并返回修改结果")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "昵称修改成功")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名或昵称为空")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    public Response<?> updateNick(@RequestBody UpdateNickRequest request) {
        try {
            String username = request.getUsername();
            String nickname = request.getNickname();
            if(username == null ||  nickname == null){
                return Response.error(ErrCode.BAD_REQUEST.getCode(), "用户名、昵称不能为空");
            }
            return Response.success(userService.updateNick(nickname, username));
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
}
