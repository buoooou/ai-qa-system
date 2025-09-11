package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.domain.entity.User;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
    boolean existsByUsername(String username);
    void updateNickname(String username, String newNickname);
}
