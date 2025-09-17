package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.AuthRequest;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.domain.model.User;

/**
 * 用户服务接口
 */
public interface userService {

    /**
     * 用户登录
     * @param authRequest 登录请求参数
     * @return 登录响应（包含令牌）
     */
    AuthResponse login(AuthRequest authRequest);

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @return 注册的用户信息
     */
    User register(String username, String password);

    /**
     * 用户退出
     * @param token 用户令牌
     * @return 是否成功退出
     */
    boolean logout(String token);

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
}
