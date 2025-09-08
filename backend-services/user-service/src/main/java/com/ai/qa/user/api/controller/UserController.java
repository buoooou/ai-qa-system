package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.Response;
import com.ai.qa.user.domain.entity.User;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public interface UserController {
    
    @PostMapping("/login")
    Response<User> login(@RequestParam String username, @RequestParam String password);

    @PostMapping("/register")
    Response<Boolean> register(@Valid @RequestBody User user);

    @PutMapping("/updateNick")
    Response<Boolean> updateNick(@RequestParam String nick, @RequestParam String userId);
}