// com/ai/qa/user/application/UserService.java
package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.application.dto.UserDTO;

public interface UserService {
    UserDTO registerUser(RegisterRequest request);
    boolean existsByUsername(String username);
    void updateNickname(String username, String newNickname);
    UserDTO getUserById(Long userId);
}
