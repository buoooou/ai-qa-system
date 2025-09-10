package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.*;

public interface UserService {
    
    LoginResponse login(UserLoginRequest request);
    
    UserResponse register(UserRegisterRequest request);
    
    UserResponse updateNickname(Long userId, UpdateNicknameRequest request);
    
    UserResponse getUserById(Long userId);
    
    UserResponse getUserByUsername(String username);
}
