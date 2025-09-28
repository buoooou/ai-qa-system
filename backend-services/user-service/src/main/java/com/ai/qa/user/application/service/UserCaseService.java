package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.response.LoginRsponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.UserInfo;
import com.ai.qa.user.application.dto.SaveRegisterCommand;

public interface UserCaseService {
    /**
     * 登录
     * 
     * @param username 登录用户名
     * @return LoginRsponse
     */
    LoginRsponse login(String username);

    /**
     * 注册
     * 
     * @param SaveRegisterCommand 注册DTO
     * @return RegisterResponse
     */
    RegisterResponse register(SaveRegisterCommand command);

    /**
     * 更新用户名
     * 
     * @param String     现有用户名
     * @param updatename 新用户名
     * @return boolean
     */
    boolean updateNick(String username, String updatename);

    /**
     * 取得用户信息
     * 
     * @param token
     * @return UserInfo
     */
    UserInfo getUserName(String token);
    
    /**
     * 取得用户名
     * 
     * @param userId
     * @return UserInfo
     */
    String getUserNamebyId(Long userId);
}
