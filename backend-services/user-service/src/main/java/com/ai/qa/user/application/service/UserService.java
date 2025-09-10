package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.ChangePasswordRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.ChangePasswordResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.domain.entity.User;

/**
 * 用户服务接口
 * 定义用户相关业务逻辑，包括登录认证与注册管理
 * 作为业务层接口，负责核心规则与数据处理
 *
 * @author Chen Guoping
 * @version 1.0
 */
public interface UserService {

    /**
     * 用户登录
     * 校验用户名密码，生成令牌并返回用户信息
     *
     * @param loginRequest 登录请求参数
     * @return 登录响应结果
     * @throws com.ai.qa.user.api.exception.BusinessException 用户不存在或密码错误时抛出
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     * 创建新账户，校验用户名唯一性及密码一致性
     *
     * @param registerRequest 注册请求参数
     * @return 注册响应结果
     * @throws com.ai.qa.user.api.exception.BusinessException 用户名已存在或密码不一致时抛出
     */
    RegisterResponse register(RegisterRequest registerRequest);

    /**
     * 根据用户名查询用户
     * 获取指定用户名的完整信息
     *
     * @param username 用户名
     * @return 用户实体，不存在则返回 null
     */
    User findByUsername(String username);

    /**
     * 检查用户名是否已存在
     * 注册前调用，验证用户名可用性
     *
     * @param username 用户名
     * @return true 已存在，false 可用
     */
    Boolean existsByUsername(String username);

    /**
     * 修改用户密码
     *
     * @param request 包含当前密码、新密码、确认密码
     * @return 修改结果响应
     * @throws BusinessException 当前密码错误、新密码不一致、用户不存在等
     */
    ChangePasswordResponse changePassword(ChangePasswordRequest request);

}