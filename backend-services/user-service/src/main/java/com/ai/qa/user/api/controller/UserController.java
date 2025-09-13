package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.AuthRequest;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.application.userService;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/***
 * 为什么user-service必须也要自己做安全限制？
 * 1。零信任网络 (Zero Trust Network)：在微服务架构中，你必须假设内部网络是不安全的。不能因为一个请求来自API Gateway就完全信任它。万一有其他内部服务被攻破，它可能会伪造请求直接调用user-service，绕过Gateway。如果user-service没有自己的安全防线，它就会被完全暴露。
 * 2。职责分离 (Separation of Concerns)：Gateway的核心职责是路由、限流、熔断和边缘认证。而user-service的核心职责是处理用户相关的业务逻辑，业务逻辑与谁能执行它是密不可分的。授权逻辑是业务逻辑的一部分，必须放在离业务最近的地方。
 * 3。细粒度授权 (Fine-Grained Authorization)：Gateway通常只做粗粒度的授权，比如“USER角色的用户可以访问/api/users/**这个路径”。但它无法知道更精细的业务规则，例如：
 * GET /api/users/{userId}: 用户123是否有权查看用户456的资料？
 * PUT /api/users/{userId}: 只有用户自己或者管理员才能修改用户信息。
 * 这些判断必须由user-service结合自身的业务逻辑和数据来完成。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final userService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(userService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录接口
     * @param authRequest 登录请求参数
     * @return 登录响应（包含令牌）
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // 调用服务层进行登录验证
        AuthResponse authResponse = userService.login(authRequest);
        // 返回成功响应，包含令牌
        return ResponseEntity.ok(authResponse);
    }

    /**
     * 用户退出接口
     * @param token 用户令牌（通过请求头Authorization传递）
     * @return 是否退出成功
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Boolean>> logout(@RequestHeader("Authorization") String token) {
        // 移除"Bearer "前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 调用服务层进行退出处理
        boolean success = userService.logout(token);
        // 返回退出结果
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户ID获取用户信息接口
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        // 调用服务层获取用户信息
        User user = userService.getUserById(userId);
        // 返回用户信息
        return ResponseEntity.ok(user);
    }

    /**
     * 获取当前登录用户信息接口
     * @param token 用户令牌（通过请求头Authorization传递）
     * @return 当前登录用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        // 移除"Bearer "前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 从token中提取用户名
        String username = jwtUtil.extractUsername(token);
        // 调用服务层获取用户信息
        User user = userService.getUserByUsername(username);
        // 返回用户信息
        return ResponseEntity.ok(user);
    }

    /**
     * 用户注册接口
     * @param authRequest 注册请求参数
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody AuthRequest authRequest) {
        // 调用服务层进行用户注册
        User user = userService.register(authRequest.getUsername(), authRequest.getPassword());
        // 返回注册成功的用户信息
        return ResponseEntity.ok(user);
    }
}
