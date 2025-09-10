package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UserResponseDto;

/*
用户服务类
 */
public interface UserService {

    UserResponseDto<Long> login(LoginReqDto requestDTO);

    UserResponseDto<Long> register(RegisterReqDto registerDTO);

    UserResponseDto<String> updateNickname(Long userId, String nickname);

}
