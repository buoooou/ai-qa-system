package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.api.dto.UserRequest;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.application.userService;
import com.ai.qa.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private userService UserService;
    
    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        User user = UserService.login(username, password);
        if (user != null) {
            // 登录成功，返回用户信息和token占位符
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", "JWT_TOKEN_PLACEHOLDER"); // 实际应该由网关生成JWT token
            return Response.success(result);
        }
        return Response.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public Response<UserResponse> register(@Valid @RequestBody UserRequest userRequest) {
        User registeredUser = UserService.register(userRequest.getUsername(), userRequest.getPassword(), userRequest.getNick());
        if (registeredUser != null) {
            UserResponse response = new UserResponse();
            response.setId(registeredUser.getId());
            response.setUsername(registeredUser.getUsername());
            response.setNick(registeredUser.getNick());
            response.setCreateTime(registeredUser.getCreateTime());
            response.setUpdateTime(registeredUser.getUpdateTime());
            return Response.success(response);
        }
        return Response.error("注册失败，用户名可能已存在");
    }

    @PutMapping("/updateNick")
    public Response<Boolean> updateNick(@RequestParam String nick, @RequestParam Long userId) {
        boolean result = UserService.updateNick(nick, userId);
        return Response.success(result);
    }
    
    @GetMapping("/getUserById")
    public Response<UserResponse> getUserById(@RequestParam("userId") Long userId) {
        User user = UserService.getUserById(userId);
        if (user != null) {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setNick(user.getNick());
            response.setCreateTime(user.getCreateTime());
            response.setUpdateTime(user.getUpdateTime());
            return Response.success(response);
        }
        return Response.error("用户不存在");
    }
}