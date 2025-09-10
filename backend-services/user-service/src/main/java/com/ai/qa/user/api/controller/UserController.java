package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.application.userService;
import com.ai.qa.user.security.JwtUtil;
import com.ai.qa.user.api.dto.UserApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final userService userService;
    private final JwtUtil jwtUtil;

    public UserController(userService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "注册失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<User>> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(UserApiResponse.success(user));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取JWT Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "登录失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<LoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.ok(UserApiResponse.success(loginResponse));
    }

    /**
     * 更新用户昵称
     */
    @PutMapping("/update-nick")
    @Operation(summary = "更新用户昵称", description = "更新当前登录用户的昵称")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "更新失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<String>> updateNick(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateNickRequest request) {
        // 从Authorization头中提取token并验证
        String token = authorization.replace("Bearer ", "");
        Long currentUserId = jwtUtil.getUserIdFromToken(token);

        userService.updateNickName(currentUserId, request);
        return ResponseEntity.ok(UserApiResponse.success("昵称更新成功"));
    }
}
