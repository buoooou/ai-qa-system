package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.*;

public interface UserService {

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 用户信息DTO
     */
    UserResponseDTO register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息
     * @return JWT token
     */
    String login(UserLoginDTO dto);

    /**
     * 修改昵称
     * @param id 用户ID
     * @param nickname 新昵称
     * @return 修改后的用户信息DTO
     */
    UserResponseDTO updateNickname(Long id, String nickname);

    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);
}
