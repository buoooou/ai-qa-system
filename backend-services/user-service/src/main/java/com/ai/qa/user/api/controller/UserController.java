package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.dto.LoginRequest;
import com.ai.qa.user.dto.LoginResponse;
import com.ai.qa.user.dto.RegisterRequest;
import com.ai.qa.user.dto.UserResponse;
import com.ai.qa.user.application.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户控制器
 * 
 * 提供用户相关的REST API接口：
 * 1. POST /api/user/register - 用户注册
 * 2. POST /api/user/login - 用户登录
 * 3. GET /api/user/{userId} - 获取用户信息
 * 4. PUT /api/user/{userId} - 更新用户信息
 * 5. PUT /api/user/{userId}/password - 修改密码
 * 6. PUT /api/user/{userId}/disable - 禁用用户
 * 7. PUT /api/user/{userId}/enable - 启用用户
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Slf4j                      // Lombok注解：自动生成日志对象
@RestController             // Spring注解：标识这是一个REST控制器
@RequestMapping("/api/user") // 设置基础路径
@RequiredArgsConstructor    // Lombok注解：为final字段生成构造函数
@Validated                  // 启用方法级别的参数验证
public class UserController {
    
    /**
     * 用户服务层接口
     * 
     * 依赖抽象而不是具体实现，遵循依赖倒置原则
     * Spring会自动注入IUserService的实现类
     */
    private final IUserService userService;
    
    /**
     * 用户注册
     * 
     * 接收用户注册信息，进行验证后创建新用户
     * 
     * @param request 注册请求信息
     * @return Response<UserResponse> 注册结果
     */
    @PostMapping("/register")
    public Response<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到用户注册请求，用户名: {}, 邮箱: {}", request.getUsername(), request.getEmail());
        
        try {
            UserResponse userResponse = userService.register(request);
            
            log.info("用户注册成功，用户ID: {}, 用户名: {}", 
                    userResponse.getId(), userResponse.getUsername());
            
            return Response.success("注册成功", userResponse);
            
        } catch (RuntimeException e) {
            log.error("用户注册失败，用户名: {}, 错误信息: {}", 
                     request.getUsername(), e.getMessage());
            return Response.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     * 
     * 验证用户凭据，成功后返回用户信息和访问令牌
     * 
     * @param request 登录请求信息
     * @return Response<LoginResponse> 登录结果
     */
    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到用户登录请求，用户名: {}", request.getUsername());
        
        try {
            LoginResponse loginResponse = userService.login(request);
            
            log.info("用户登录成功，用户ID: {}, 用户名: {}", 
                    loginResponse.getUser().getId(), loginResponse.getUser().getUserName());
            
            return Response.success("登录成功", loginResponse);
            
        } catch (RuntimeException e) {
            log.error("用户登录失败，用户名: {}, 错误信息: {}", 
                     request.getUsername(), e.getMessage());
            return Response.error(e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return Response<UserResponse> 用户信息
     */
    @GetMapping("/{userId}")
    public Response<UserResponse> getUserById(@PathVariable @NotNull @Min(1) Long userId) {
        log.debug("收到获取用户信息请求，用户ID: {}", userId);
        
        try {
            UserResponse userResponse = userService.getUserById(userId);
            
            if (userResponse != null) {
                log.debug("获取用户信息成功，用户ID: {}", userId);
                return Response.success("获取成功", userResponse);
            } else {
                log.warn("用户不存在，用户ID: {}", userId);
                return Response.notFound("用户不存在");
            }
            
        } catch (Exception e) {
            log.error("获取用户信息失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return Response.error("获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return Response<UserResponse> 用户信息
     */
    @GetMapping("/username/{username}")
    public Response<UserResponse> getUserByUsername(@PathVariable String username) {
        log.debug("收到根据用户名获取用户信息请求，用户名: {}", username);
        
        try {
            UserResponse userResponse = userService.getUserByUsername(username);
            
            if (userResponse != null) {
                log.debug("根据用户名获取用户信息成功，用户名: {}", username);
                return Response.success("获取成功", userResponse);
            } else {
                log.warn("用户不存在，用户名: {}", username);
                return Response.notFound("用户不存在");
            }
            
        } catch (Exception e) {
            log.error("根据用户名获取用户信息失败，用户名: {}, 错误信息: {}", username, e.getMessage());
            return Response.error("获取失败：" + e.getMessage());
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
    public Response<UserResponse> updateUserInfo(
            @PathVariable @NotNull @Min(1) Long userId,
            @RequestParam String email) {
        
        log.info("收到更新用户信息请求，用户ID: {}, 新邮箱: {}", userId, email);
        
        try {
            UserResponse userResponse = userService.updateUserInfo(userId, email);
            
            if (userResponse != null) {
                log.info("更新用户信息成功，用户ID: {}", userId);
                return Response.success("更新成功", userResponse);
            } else {
                log.warn("更新用户信息失败，用户不存在，用户ID: {}", userId);
                return Response.notFound("用户不存在");
            }
            
        } catch (Exception e) {
            log.error("更新用户信息失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return Response.error("更新失败：" + e.getMessage());
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
    public Response<Boolean> changePassword(
            @PathVariable @NotNull @Min(1) Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        log.info("收到修改密码请求，用户ID: {}", userId);
        
        try {
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            
            if (success) {
                log.info("修改密码成功，用户ID: {}", userId);
                return Response.success("密码修改成功", true);
            } else {
                log.warn("修改密码失败，用户ID: {}", userId);
                return Response.error("密码修改失败");
            }
            
        } catch (Exception e) {
            log.error("修改密码异常，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return Response.error("密码修改失败：" + e.getMessage());
        }
    }
    
    /**
     * 禁用用户
     * 
     * @param userId 用户ID
     * @return Response<Boolean> 操作结果
     */
    @PutMapping("/{userId}/disable")
    public Response<Boolean> disableUser(@PathVariable @NotNull @Min(1) Long userId) {
        log.info("收到禁用用户请求，用户ID: {}", userId);
        
        try {
            boolean success = userService.disableUser(userId);
            
            if (success) {
                log.info("禁用用户成功，用户ID: {}", userId);
                return Response.success("用户已禁用", true);
            } else {
                log.warn("禁用用户失败，用户ID: {}", userId);
                return Response.error("禁用失败");
            }
            
        } catch (Exception e) {
            log.error("禁用用户异常，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return Response.error("禁用失败：" + e.getMessage());
        }
    }
    
    /**
     * 启用用户
     * 
     * @param userId 用户ID
     * @return Response<Boolean> 操作结果
     */
    @PutMapping("/{userId}/enable")
    public Response<Boolean> enableUser(@PathVariable @NotNull @Min(1) Long userId) {
        log.info("收到启用用户请求，用户ID: {}", userId);
        
        try {
            boolean success = userService.enableUser(userId);
            
            if (success) {
                log.info("启用用户成功，用户ID: {}", userId);
                return Response.success("用户已启用", true);
            } else {
                log.warn("启用用户失败，用户ID: {}", userId);
                return Response.error("启用失败");
            }
            
        } catch (Exception e) {
            log.error("启用用户异常，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return Response.error("启用失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return Response<Boolean> 检查结果
     */
    @GetMapping("/check/username/{username}")
    public Response<Boolean> checkUsernameExists(@PathVariable String username) {
        log.debug("收到检查用户名是否存在请求，用户名: {}", username);
        
        try {
            boolean exists = userService.existsByUsername(username);
            
            log.debug("检查用户名是否存在完成，用户名: {}, 存在: {}", username, exists);
            return Response.success("检查完成", exists);
            
        } catch (Exception e) {
            log.error("检查用户名是否存在失败，用户名: {}, 错误信息: {}", username, e.getMessage());
            return Response.error("检查失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱地址
     * @return Response<Boolean> 检查结果
     */
    @GetMapping("/check/email/{email}")
    public Response<Boolean> checkEmailExists(@PathVariable String email) {
        log.debug("收到检查邮箱是否存在请求，邮箱: {}", email);
        
        try {
            boolean exists = userService.existsByEmail(email);
            
            log.debug("检查邮箱是否存在完成，邮箱: {}, 存在: {}", email, exists);
            return Response.success("检查完成", exists);
            
        } catch (Exception e) {
            log.error("检查邮箱是否存在失败，邮箱: {}, 错误信息: {}", email, e.getMessage());
            return Response.error("检查失败：" + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     * 
     * 用于检查用户服务是否正常运行
     * 
     * @return Response<String> 服务状态
     */
    @GetMapping("/health")
    public Response<String> health() {
        return Response.success("User Service is running", "User Service is running");
    }
    
    /**
     * 获取服务信息
     * 
     * 返回用户服务的基本信息
     * 
     * @return Response<Map<String, Object>> 服务信息
     */
    @GetMapping("/info")
    public Response<java.util.Map<String, Object>> getServiceInfo() {
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        info.put("service", "User Service");
        info.put("version", "1.0");
        info.put("description", "AI智能问答系统用户管理服务");
        info.put("features", java.util.Arrays.asList(
            "用户注册", "用户登录", "信息管理", "密码修改", "状态管理"
        ));
        
        return Response.success("获取服务信息成功", info);
    }
}
