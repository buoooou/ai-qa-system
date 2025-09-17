package com.ai.qa.user.application.service;

import com.ai.qa.user.application.dto.SaveRegisterCommand;

public interface UserCaseService {
    /**
     * 登录
     * 
     * @param username 登录用户名
     * @return String token
     */
    String login(String username);

    /**
     * 注册
     * 
     * @param SaveRegisterCommand 注册DTO
     * @return boolean
     */
    boolean register(SaveRegisterCommand command);

    /**
     * 更新用户名
     * 
     * @param String     现有用户名
     * @param updatename 新用户名
     * @return boolean
     */
    boolean updateNick(String username, String updatename);

    /**
     * 取得用户名
     * 
     * @param userId  用户名ID
     * @return String
     */
    String getUserName(Long userId);
}
