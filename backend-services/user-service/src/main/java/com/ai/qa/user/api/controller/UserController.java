package com.ai.qa.user.api.controller;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.ApiResponse;
import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.api.exception.UserServiceException;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * *
 * 为什么user-service必须也要自己做安全限制？ 1。零信任网络 (Zero Trust
 * Network)：在微服务架构中，你必须假设内部网络是不安全的。不能因为一个请求来自API
 * Gateway就完全信任它。万一有其他内部服务被攻破，它可能会伪造请求直接调用user-service，绕过Gateway。如果user-service没有自己的安全防线，它就会被完全暴露。
 * 2。职责分离 (Separation of
 * Concerns)：Gateway的核心职责是路由、限流、熔断和边缘认证。而user-service的核心职责是处理用户相关的业务逻辑，业务逻辑与谁能执行它是密不可分的。授权逻辑是业务逻辑的一部分，必须放在离业务最近的地方。
 * 3。细粒度授权 (Fine-Grained
 * Authorization)：Gateway通常只做粗粒度的授权，比如“USER角色的用户可以访问/api/users/**这个路径”。但它无法知道更精细的业务规则，例如：
 * GET /api/users/{userId}: 用户123是否有权查看用户456的资料？ PUT /api/users/{userId}:
 * 只有用户自己或者管理员才能修改用户信息。 这些判断必须由user-service结合自身的业务逻辑和数据来完成。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     *
     * 验证用户凭据，成功后返回用户信息和访问令牌
     *
     * @param request 登录请求信息
     * @return Response<LoginResponse> 登录结果
     */
    @Operation(summary = "用户登录")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户登录成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户认证失败"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "该用户不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("[User-Service] [{}]## Method {} Start.", this.getClass().getSimpleName(), "login");
        System.out.println("测试login");

        try {
            LoginResponse loginResponse = userService.login(request);

            log.info("用户登录成功，用户ID: {}, 用户名: {}", loginResponse.getUser().getId(), loginResponse.getUser().getUsername());

            return ApiResponse.success(HttpStatus.OK.value(), Constants.MSG_USER_LOGIN_SUCCESS, loginResponse);
        } catch (UserServiceException e) {
            log.error("用户登录失败，用户名: {}, 错误信息: {}", request.getUsername(), e.getMessage());
            return ApiResponse.error(e.getErrorCode());
        }

    }

    /**
     * 用户注册
     *
     * 接收用户注册信息，进行验证后创建新用户
     *
     * @param request 注册请求信息
     * @return Response<UserResponse> 注册结果
     */
    @Operation(summary = "用户注册")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "用户注册成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "该用户已存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("[User-Service] [{}]## Method {} Start.", this.getClass().getSimpleName(), "register");
        System.out.println("测试register");

        try {
            log.info("收到用户注册请求，用户名: {}", request.getUsername());
            UserResponse userResponse = userService.register(request);
            log.info("用户注册成功，用户ID: {}, 用户名: {}", userResponse.getId(), userResponse.getUsername());

            return ApiResponse.success(HttpStatus.CREATED.value(), Constants.MSG_USER_REGISTER_SUCCESS, userResponse);

        } catch (UserServiceException e) {
            log.error("[User-Service] [{}]## {} code:{}, username:{}", this.getClass().getSimpleName(), Constants.MSG_USER_REGISTER_FAIL, e.getErrorCode().getCode(), request.getUsername());
            return ApiResponse.error(e.getErrorCode());
        }

    }

    @Operation(summary = "修改昵称")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户注册成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "该用户不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PutMapping("/{username}/nickname")
    public ApiResponse<UserResponse> updateNickname(@PathVariable String username, @PathVariable String nickname) {
        log.debug("[User-Service] [{}]## Method {} Start.", this.getClass().getSimpleName(), "updateNickname");
        System.out.println("测试updateNickname");

        try {
            UserResponse userResponse = userService.updateNickname(username, nickname);
            log.debug("[User-Service] [{}]## username:{}, nickname:{}", this.getClass().getSimpleName(), username, nickname);
            return ApiResponse.success(HttpStatus.OK.value(), Constants.MSG_NICKNAME_UPDATE_SUCCESS, userResponse);
        } catch (EntityNotFoundException e) {
            log.error("[User-Service] [{}]## {} code:{}, username:{}", this.getClass().getSimpleName(), Constants.MSG_USER_NOT_FOUND, HttpStatus.NOT_FOUND.value(), username);
            throw e;
        }
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return Response<UserResponse> 用户信息
     */
    @Operation(summary = "根据用户ID获得该用户详细信息")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户信息取得成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "该用户不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable @Min(1) Long userId) {
        log.debug("收到获取用户信息请求，用户ID: {}", userId);

        try {
            UserResponse userResponse = userService.getUserById(userId);

            log.debug("获取用户信息成功，用户ID: {}", userId);
            return ApiResponse.success(HttpStatus.OK.value(), "获取成功", userResponse);

        } catch (EntityNotFoundException e) {
            log.error("获取用户信息失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return Response<UserResponse> 用户信息
     */
    @GetMapping("/username/{username}")
    public ApiResponse<UserResponse> getUserByUsername(@PathVariable String username) {
        log.debug("收到根据用户名获取用户信息请求，用户名: {}", username);

        try {
            UserResponse userResponse = userService.getUserByUsername(username);

            log.debug("根据用户名获取用户信息成功，用户名: {}", username);
            return ApiResponse.success(HttpStatus.OK.value(), "获取成功", userResponse);

        } catch (EntityNotFoundException e) {
            log.error("根据用户名获取用户信息失败，用户名: {}, 错误信息: {}", username, e.getMessage());
            throw e;
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param email 新邮箱地址
     * @return Response<UserResponse> 更新后的用户信息
     */
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUserInfo(
            @PathVariable @NotNull @Min(1) Long userId,
            @RequestParam String email) {

        log.info("收到更新用户信息请求，用户ID: {}, 新邮箱: {}", userId, email);

        try {
            UserResponse userResponse = userService.updateUserInfo(userId, email);

            log.info("更新用户信息成功，用户ID: {}", userId);
            return ApiResponse.success(HttpStatus.OK.value(), "更新成功", userResponse);

        } catch (EntityNotFoundException e) {
            log.error("更新用户信息失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return Response<Boolean> 修改结果
     */
    @PutMapping("/{userId}/password")
    public ApiResponse<Boolean> changePassword(
            @PathVariable @NotNull @Min(1) Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        log.info("收到修改密码请求，用户ID: {}", userId);

        try {
            boolean success = userService.changePassword(userId, oldPassword, newPassword);

            log.info("修改密码成功，用户ID: {}", userId);
            return ApiResponse.success(HttpStatus.OK.value(), "密码修改成功", true);

        } catch (EntityNotFoundException e) {
            log.error("修改密码异常，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            throw e;
        }
    }
}
