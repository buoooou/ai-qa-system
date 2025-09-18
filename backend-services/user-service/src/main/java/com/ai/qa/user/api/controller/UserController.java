package com.ai.qa.user.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.common.CommonUtil;
import com.ai.qa.user.domain.model.UserDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User API", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码验证用户身份")
    public Response<?> login(@RequestBody LoginRequest request) {
        try {
            UserDto userDto = userService.findByUsername(request.getUsername());
            if (userDto != null && passwordEncoder.matches(request.getPassword(), userDto.getPassword())) {
                return Response.success(userDto);
            } else {
                return Response.error(ErrCode.UNAUTHORIZED.getCode(), ErrCode.UNAUTHORIZED.getMessage());
            }
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public String getUserById(@PathVariable("userId") Long userId){
        UserDto userDto = userService.findByUserID(userId);
        if(userDto != null){
            return "exist";
        } else {
            return "not exist";
        }
    }

    @PutMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户并返回注册结果")
    public Response<?> register(@RequestBody RegisterRequest request) {
        try {
            String username = request.getUsername();
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            String nick = request.getNick();
            // 如果注册时未填写昵称，使用系统默认昵称
            if(nick == null || nick.length() == 0) {
                nick = CommonUtil.generateDefaultNick();
            }
            UserDto userDto = userService.register(username, encodedPassword, nick);
            return Response.success(userDto);
        } catch (RuntimeException e) {
            return Response.error(ErrCode.BAD_REQUEST.getCode(), e.getMessage());
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "用户修改昵称", description = "修改用户昵称并返回修改结果")
    public Response<?> updateNick(String nick, Long userId) {
        try {
            int result = userService.updateNick(nick, userId);
            if (result == 1) {
                return Response.success(true);
            } else {
                return Response.error(ErrCode.BAD_REQUEST.getCode(), "昵称修改失败");
            }
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
}
