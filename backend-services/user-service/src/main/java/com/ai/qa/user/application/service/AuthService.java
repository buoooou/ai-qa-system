package com.ai.qa.user.application.service;

import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.LoginResponse;

@Service
public interface AuthService {
    
    LoginResponse authenticate(String username, String password);
}
