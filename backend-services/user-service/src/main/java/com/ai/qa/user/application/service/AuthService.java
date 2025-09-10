package com.ai.qa.user.application.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.AuthResponseDTO;

@Service
public interface AuthService {
    
    AuthResponseDTO authenticate(String username, String password) throws AuthenticationException;
}
