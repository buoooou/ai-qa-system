package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UserResponseDto;

public interface UserService {

    UserResponseDto login(LoginReqDto requestDTO);

    UserResponseDto register(RegisterReqDto registerDTO);

}
