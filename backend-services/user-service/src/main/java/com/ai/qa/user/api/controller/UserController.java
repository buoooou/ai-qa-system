package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.api.dto.UserRequest;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        logger.info("[LOGIN] 接收到登录请求 - 用户名: {}, 请求IP: {}, User-Agent: {}", 
                   username, request.getRemoteAddr(), request.getHeader("User-Agent"));
        logger.info("[LOGIN] 请求头信息: Authorization={}, Content-Type={}", 
                   request.getHeader("Authorization"), request.getHeader("Content-Type"));
        
        try {
            User user = userService.login(username, password);
            if (user != null) {
                logger.info("[LOGIN] 登录成功 - 用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
                // 登录成功，返回用户信息和token占位符
                Map<String, Object> result = new HashMap<>();
                result.put("user", user);
                result.put("token", "JWT_TOKEN_PLACEHOLDER"); // 实际应该由网关生成JWT token
                return Response.success(result);
            }
            logger.warn("[LOGIN] 登录失败 - 用户名或密码错误: {}", username);
            return Response.error("用户名或密码错误");
        } catch (Exception e) {
            logger.error("[LOGIN] 登录过程中发生异常 - 用户名: {}, 异常: {}", username, e.getMessage(), e);
            return Response.error("登录过程中发生错误");
        }
    }

    @PostMapping("/register")
    public Response<UserResponse> register(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
        logger.info("[REGISTER] 接收到注册请求 - 用户名: {}, 昵称: {}, 请求IP: {}", 
                   userRequest.getUsername(), userRequest.getNick(), request.getRemoteAddr());
        logger.info("[REGISTER] 请求头信息: Authorization={}, Content-Type={}", 
                   request.getHeader("Authorization"), request.getHeader("Content-Type"));
        
        try {
            User registeredUser = userService.register(userRequest.getUsername(), userRequest.getPassword(), userRequest.getNick());
            if (registeredUser != null) {
                logger.info("[REGISTER] 注册成功 - 用户ID: {}, 用户名: {}", registeredUser.getId(), registeredUser.getUsername());
                UserResponse response = new UserResponse();
                response.setId(registeredUser.getId());
                response.setUsername(registeredUser.getUsername());
                response.setNick(registeredUser.getNick());
                response.setCreateTime(registeredUser.getCreateTime());
                response.setUpdateTime(registeredUser.getUpdateTime());
                return Response.success(response);
            }
            logger.warn("[REGISTER] 注册失败 - 用户名可能已存在: {}", userRequest.getUsername());
            return Response.error("注册失败，用户名可能已存在");
        } catch (Exception e) {
            logger.error("[REGISTER] 注册过程中发生异常 - 用户名: {}, 异常: {}", userRequest.getUsername(), e.getMessage(), e);
            return Response.error("注册过程中发生错误");
        }
    }

    @PutMapping("/updateNick")
    public Response<Boolean> updateNick(@RequestParam String nick, @RequestParam Long userId, HttpServletRequest request) {
        logger.info("[UPDATE_NICK] 接收到更新昵称请求 - 用户ID: {}, 新昵称: {}, 请求IP: {}", 
                   userId, nick, request.getRemoteAddr());
        logger.info("[UPDATE_NICK] 请求头信息: Authorization={}, Content-Type={}", 
                   request.getHeader("Authorization"), request.getHeader("Content-Type"));
        
        try {
            boolean result = userService.updateNick(nick, userId);
            logger.info("[UPDATE_NICK] 更新昵称结果 - 用户ID: {}, 结果: {}", userId, result);
            return Response.success(result);
        } catch (Exception e) {
            logger.error("[UPDATE_NICK] 更新昵称过程中发生异常 - 用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return Response.error("更新昵称过程中发生错误");
        }
    }
    
    @GetMapping("/getUserById")
    public Response<UserResponse> getUserById(@RequestParam("userId") Long userId, HttpServletRequest request) {
        logger.info("[GET_USER_BY_ID] 接收到获取用户请求 - 用户ID: {}, 请求IP: {}", 
                   userId, request.getRemoteAddr());
        logger.info("[GET_USER_BY_ID] 请求头信息: Authorization={}, Content-Type={}", 
                   request.getHeader("Authorization"), request.getHeader("Content-Type"));
        
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                logger.info("[GET_USER_BY_ID] 获取用户成功 - 用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
                UserResponse response = new UserResponse();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setNick(user.getNick());
                response.setCreateTime(user.getCreateTime());
                response.setUpdateTime(user.getUpdateTime());
                return Response.success(response);
            }
            logger.warn("[GET_USER_BY_ID] 用户不存在 - 用户ID: {}", userId);
            return Response.error("用户不存在");
        } catch (Exception e) {
            logger.error("[GET_USER_BY_ID] 获取用户过程中发生异常 - 用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return Response.error("获取用户过程中发生错误");
        }
    }
    
    @GetMapping("/checkNick")
    public Response<Boolean> checkNick(@RequestParam("nick") String nick, HttpServletRequest request) {
        logger.info("[CHECK_NICK] 接收到检查昵称请求 - 昵称: {}, 请求IP: {}", 
                   nick, request.getRemoteAddr());
        logger.info("[CHECK_NICK] 请求头信息: Authorization={}, Content-Type={}", 
                   request.getHeader("Authorization"), request.getHeader("Content-Type"));
        
        try {
            boolean exists = userService.isNickExists(nick);
            logger.info("[CHECK_NICK] 检查昵称结果 - 昵称: {}, 存在: {}", nick, exists);
            return Response.success(exists);
        } catch (Exception e) {
            logger.error("[CHECK_NICK] 检查昵称过程中发生异常 - 昵称: {}, 异常: {}", nick, e.getMessage(), e);
            return Response.error("检查昵称过程中发生错误");
        }
    }
}