package com.ai.qa.user.domain.repository;

import com.ai.qa.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository
 * <p>
 * 这是用户实体的持久化接口，用于操作 user 表。
 * 继承 JpaRepository，可直接使用增删改查方法。
 */
public interface UserRepository extends JpaRepository<User, Long> {

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
}
