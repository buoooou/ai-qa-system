package com.ai.qa.user.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.ApiResponse;
import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.RefreshTokenRequest;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserLoginResponse;
import com.ai.qa.user.api.exception.ErrorCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.Constants;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.model.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证控制器
 *
 * 提供JWT认证相关的REST API接口： 1. POST /api/auth/login - 用户登录（返回JWT令牌） 2. POST
 * /api/auth/refresh - 刷新访问令牌 3. POST /api/auth/logout - 用户登出 4. GET
 * /api/auth/verify - 验证令牌有效性 5. GET /api/auth/profile - 获取当前用户信息
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 访问令牌有效期（毫秒） 默认2小时
     */
    @Value("${jwt.access-expiration:7200000}")
    private long accessTokenExpiration;

    /**
     * 用户登录接口（JWT版本）
     *
     * 验证用户身份后，生成并返回JWT访问令牌和刷新令牌
     *
     * @param request 登录请求信息
     * @return Response<LoginResponse> 登录结果，包含用户信息和JWT令牌
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody UserLoginRequest request) {
        log.info("收到JWT登录请求，用户名: {}", request.getUsername());

        try {
            // 验证用户身份
            UserLoginResponse user = userService.login(request);

            // 生成JWT令牌
            String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

            // 为了安全，不返回密码信息
            user.setPassword(null);

            // 构建登录响应
            LoginResponse loginResponse = new LoginResponse();
            LoginResponse.User loginUser = loginResponse.new User();
            loginUser.setId(user.getId());
            loginUser.setUsername(user.getUsername());
            loginUser.setPassword(user.getPassword());
            loginUser.setNickname(user.getNickname());
            loginUser.setCreateTime(user.getCreateTime());
            loginUser.setUpdateTime(user.getUpdateTime());
            loginResponse.setUser(loginUser);
            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setExpiresIn(accessTokenExpiration / 1000);

            log.info("JWT登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            return ApiResponse.success(HttpStatus.OK.value(), Constants.MSG_USER_LOGIN_SUCCESS, loginResponse);

        } catch (RuntimeException e) {
            log.error("JWT登录失败，用户名: {}, 错误信息: {}", request.getUsername(), e.getMessage());
            return ApiResponse.error(ErrorCode.UNAUTHORIZED, e.getMessage());
        }
    }

    /**
     * 刷新访问令牌接口
     *
     * 使用刷新令牌获取新的访问令牌
     *
     * @param request 刷新令牌请求
     * @return Response<LoginResponse> 新的令牌信息
     */
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("收到刷新令牌请求");

        try {
            String refreshToken = request.getRefreshToken();

            // 验证刷新令牌
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                return ApiResponse.error(ErrorCode.BAD_REQUEST, "无效的刷新令牌类型");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "刷新令牌已过期，请重新登录");
            }

            // 从刷新令牌中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String username = jwtUtil.getUsernameFromToken(refreshToken);

            // 验证用户是否仍然存在
            Optional<User> userOptional = userService.findById(userId);
            if (!userOptional.isPresent()) {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "用户不存在，请重新登录");
            }

            User user = userOptional.get();

            // 生成新的访问令牌（刷新令牌保持不变）
            String newAccessToken = jwtUtil.generateAccessToken(userId, username);

            // 为了安全，不返回密码信息
            user.setPassword(null);

            // 构建响应（使用原刷新令牌）
            LoginResponse loginResponse = new LoginResponse();
            LoginResponse.User loginUser = loginResponse.new User();
            loginUser.setId(user.getId());
            loginUser.setUsername(user.getUsername());
            loginUser.setPassword(user.getPassword());
            loginUser.setNickname(user.getNickname());
            loginUser.setCreateTime(user.getCreateTime());
            loginUser.setUpdateTime(user.getUpdateTime());
            loginResponse.setUser(loginUser);
            loginResponse.setAccessToken(newAccessToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setExpiresIn(accessTokenExpiration / 1000);

            log.info("刷新令牌成功，用户ID: {}, 用户名: {}", userId, username);
            return ApiResponse.success(HttpStatus.OK.value(),"令牌刷新成功", loginResponse);

        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return ApiResponse.error(ErrorCode.UNAUTHORIZED, "刷新令牌失败，请重新登录");
        }
    }

    /**
     * 用户登出接口
     *
     * 在实际应用中，可以将令牌加入黑名单 这里简单返回成功信息，前端删除本地存储的令牌
     *
     * @param httpRequest HTTP请求
     * @return Response<String> 登出结果
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest httpRequest) {
        log.info("收到用户登出请求");

        try {
            // 从请求头中获取令牌
            String token = extractTokenFromRequest(httpRequest);

            if (token != null) {
                String username = jwtUtil.getUsernameFromToken(token);
                log.info("用户登出成功，用户名: {}", username);

                // tokenBlacklistService.addToBlacklist(token);
                return ApiResponse.success(HttpStatus.OK.value(),"登出成功", "登出成功");
            } else {
                return ApiResponse.success(HttpStatus.OK.value(),"登出成功", "登出成功");
            }

        } catch (Exception e) {
            log.error("用户登出处理失败: {}", e.getMessage());
            return ApiResponse.success(HttpStatus.OK.value(),"登出成功", "登出成功"); // 即使处理失败也返回成功
        }
    }

    /**
     * 验证令牌有效性接口
     *
     * 验证访问令牌是否有效
     *
     * @param httpRequest HTTP请求
     * @return Response<String> 验证结果
     */
    @GetMapping("/verify")
    public ApiResponse<String> verifyToken(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);

            if (token == null) {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "缺少访问令牌");
            }

            if (!jwtUtil.isAccessToken(token)) {
                return ApiResponse.error(ErrorCode.BAD_REQUEST, "无效的令牌类型");
            }

            if (jwtUtil.isTokenExpired(token)) {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "访问令牌已过期");
            }

            String username = jwtUtil.getUsernameFromToken(token);

            if (jwtUtil.validateToken(token, username)) {
                return ApiResponse.success(HttpStatus.OK.value(),"令牌有效", "令牌有效");
            } else {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "无效的访问令牌");
            }

        } catch (Exception e) {
            log.error("验证令牌失败: {}", e.getMessage());
            return ApiResponse.error(ErrorCode.UNAUTHORIZED, "令牌验证失败");
        }
    }

    /**
     * 获取当前用户信息接口
     *
     * 根据访问令牌获取当前登录用户的信息
     *
     * @param httpRequest HTTP请求
     * @return Response<User> 用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<User> getCurrentUserProfile(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);

            if (token == null) {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "缺少访问令牌");
            }

            if (!jwtUtil.isAccessToken(token) || jwtUtil.isTokenExpired(token)) {
                return ApiResponse.error(ErrorCode.UNAUTHORIZED, "无效或已过期的访问令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Optional<User> userOptional = userService.findById(userId);

            if (!userOptional.isPresent()) {
                return ApiResponse.error(ErrorCode.USER_NOT_FOUND, "用户不存在");
            }

            User user = userOptional.get();
            user.setPassword(null); // 不返回密码信息

            return ApiResponse.success(HttpStatus.OK.value(),"获取用户信息成功", user);

        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.error(ErrorCode.UNAUTHORIZED, "获取用户信息失败");
        }
    }

    /**
     * 从HTTP请求中提取JWT令牌
     *
     * @param request HTTP请求
     * @return String JWT令牌，如果不存在则返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 移除"Bearer "前缀
        }

        return null;
    }
}
