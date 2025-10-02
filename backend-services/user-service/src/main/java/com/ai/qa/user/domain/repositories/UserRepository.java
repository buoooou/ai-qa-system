package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.entity.User;

import java.util.Optional;

/**
 * UserRepository
 * <p>
 * 这是用户实体的持久化接口，用于操作 user 表。
 * 继承 JpaRepository，可直接使用增删改查方法。
 */
public interface UserRepository {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 如果存在，返回 Optional<User>；否则返回 Optional.empty()
     */
    Optional<User> findByUsername(String username);

    /**
     * 判断用户名是否已存在
     *
     * @param username 用户名
     * @return true 如果用户名已存在，否则 false
     */
    boolean existsByUsername(String username);

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
}
