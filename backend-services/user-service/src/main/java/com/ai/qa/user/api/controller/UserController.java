package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.application.userService;
import com.ai.qa.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private userService UserService;
    
    @PostMapping("/login")
    public Response<User> login(@RequestParam String username, @RequestParam String password) {
        User user = UserService.login(username, password);
        return Response.success(user);
    }

    @PostMapping("/register")
    public Response<Boolean> register(@Valid @RequestBody User user) {
        User registeredUser = UserService.register(user.getUsername(), user.getPassword());
        return Response.success(registeredUser != null);
    }

    @PutMapping("/updateNick")
    public Response<Boolean> updateNick(@RequestParam String nick, @RequestParam String userId) {
        boolean result = UserService.updateNick(nick, userId);
        return Response.success(result);
    }
}