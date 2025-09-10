package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserRegisterRequest;
import com.ai.qa.user.api.dto.UpdateNickRequest;
import com.ai.qa.user.domain.entity.User;

public interface userService {

    /**
     * 用户登录
     * @param request 登录请求DTO，包含用户名和密码
     * @return 登录响应DTO，包含JWT Token和用户信息
     */
    LoginResponse login(UserLoginRequest request);

    /**
     * 用户注册
     * @param request 注册请求DTO，包含用户名和密码
     * @return 注册用户实体
     */
    User register(UserRegisterRequest request);

    /**
     * 更新用户昵称
     * @param currentUserId 当前登录用户ID（从JWT Token中获取，用于权限校验）
     * @param request 更新昵称请求DTO，包含用户ID和新昵称
     */
    void updateNickName(Long currentUserId, UpdateNickRequest request);
}
