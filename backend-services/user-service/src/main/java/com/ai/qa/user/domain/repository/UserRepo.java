package com.ai.qa.user.domain.repository;

import java.util.Optional;

import com.ai.qa.user.domain.model.User;

public interface UserRepo {

    /**
     * 根据ID查找用户聚合
     *
     * @param id 用户ID
     * @return 一个包含用户（如果找到）的Optional
     */
    Optional<User> findById(Long id);

    /**
     * 保存用户聚合（用于创建或更新）
     *
     * @param user 用户聚合
     * @return 已保存的用户聚合
     */
    User save(User user);

    /**
     * 根据用户名查找用户聚合
     *
     * @param id 用户ID
     * @return 一个包含用户（如果找到）的Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户名判断用户是否存在
     *
     * @param id 用户ID
     * @return 一个包含用户（如果找到）的Optional
     */
    boolean existsByUsername(String username);

    /**
     * 根据用户名和密码查询用户 用于登录验证，同时匹配用户名和加密后的密码
     *
     * @param username 用户名
     * @param password 加密后的密码
     * @return Optional<User> 用户信息的Optional包装
     */
    Optional<User> findByUsernameAndPassword(String username, String password);

    /**
     * 统计指定昵称的用户数量
     * 用于验证昵称的唯一性或统计使用情况
     *
     * @param nickname 昵称
     * @return Long 用户数量
     */
    Long countNickname(String nickname);
}
