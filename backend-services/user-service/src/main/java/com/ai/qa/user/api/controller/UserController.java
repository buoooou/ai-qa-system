package com.ai.qa.user.api.controller;

import org.springframework.web.bind.annotation.RequestBody;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.Response;


public interface UserController {
   Response<?> login(@RequestBody LoginRequest request);

   Response<?> register(@RequestBody RegisterRequest request);

   Response<?> updateNick(String nick,Long userId);
}
