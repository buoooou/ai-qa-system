package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    /**
     * 根据ID查找用户聚合
     * @param id 用户ID
     * @return 一个包含用户（如果找到）的Optional
     */
    Optional<User> findById(Long id);

    /**
     * 保存用户聚合（用于创建或更新）
     * @param user 用户聚合
     * @return 已保存的用户聚合
     */
    User save(User user);
}
