package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
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
    public Response<Boolean> register(@Valid @RequestBody User user) {
        User registeredUser = UserService.register(user.getUsername(), user.getPassword());
        return Response.success(registeredUser != null);
    }

    @PutMapping("/updateNick")
    public Response<Boolean> updateNick(@RequestParam String nick, @RequestParam Long userId) {
        boolean result = UserService.updateNick(nick, userId);
        return Response.success(result);
    }
    
    @GetMapping("/getUserById")
    public Response<User> getUserById(@RequestParam("userId") Long userId) {
        User user = UserService.getUserById(userId);
        if (user != null) {
            return Response.success(user);
        }
        return Response.error("用户不存在");
    }
}