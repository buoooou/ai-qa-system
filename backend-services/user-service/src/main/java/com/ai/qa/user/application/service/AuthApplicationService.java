package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.api.dto.RegisterRequest;

public interface AuthApplicationService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(String usernameOrEmail, String rawPassword);
}
