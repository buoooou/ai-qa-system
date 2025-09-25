package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.UserRequestDto;
import com.ai.qa.user.domain.entity.User;

public interface UserService {
    

    /**
     * 用户登录
     */
    User login(UserRequestDto loginRequest);

    /**
     * 用户注册
     */
    User register(UserRequestDto registerRequest);

    /**
     * 用户昵称更新
     */
    User updateNickName(UserRequestDto registerRequest);

    /**
     * 密码更新
     */
    void updatePassword(UserRequestDto registerRequest);
}
