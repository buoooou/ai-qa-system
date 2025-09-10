package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdateNickRequest;

public interface UserService {
    /**
     * 登录
     * 
     * @param LoginRequest 登录DTO
     * @return String token
     */
    String login(LoginRequest loginRequest);

    /**
     * 注册
     * 
     * @param RegisterRequest 注册DTO
     * @return boolean
     */
    boolean register(RegisterRequest registerRequest);

    /**
     * 更新用户名
     * 
     * @param String            现有用户名
     * @param UpdateNickRequest 新用户更新DTO
     * @return boolean
     */
    boolean updateNick(String username, UpdateNickRequest updateNickRequest);
}
