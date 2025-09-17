package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.application.command.UserLoginCommand;
import com.ai.qa.user.application.command.UserRegisterCommand;
import com.ai.qa.user.application.command.UserUpdateNickCommand;
import com.ai.qa.user.domain.entity.User;

public interface UserApplicationService {
    
    /**
     * 用户注册
     * @param command 注册命令
     * @return 注册用户信息
     */
    User register(UserRegisterCommand command);
    
    /**
     * 用户登录
     * @param command 登录命令
     * @return 登录响应
     */
    LoginResponse login(UserLoginCommand command);
    
    /**
     * 更新用户昵称
     * @param command 更新昵称命令
     * @param currentUserId 当前用户ID
     */
    void updateNickName(UserUpdateNickCommand command, Long currentUserId);
    
    /**
     * 生成用户向量嵌入
     * @param userId 用户ID
     */
    void generateUserVectorEmbedding(Long userId);
}