package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.application.userService;
import com.ai.qa.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {
    
    @Autowired
    private userService UserService;
    
    @Override
    public Response<User> login(String username, String password) {
        User user = UserService.login(username, password);
        return Response.success(user);
    }

    @Override
    public Response<Boolean> register(User user) {
        User registeredUser = UserService.register(user.getUsername(), user.getPassword());
        return Response.success(registeredUser != null);
    }

    @Override
    public Response<Boolean> updateNick(String nick, String userId) {
        boolean result = UserService.updateNick(nick, userId);
        return Response.success(result);
    }
}