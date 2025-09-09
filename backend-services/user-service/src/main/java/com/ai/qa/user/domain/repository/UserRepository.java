package com.ai.qa.user.domain.repository;

import com.ai.qa.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 * 继承 JpaRepository 提供基础 CRUD，并扩展用户相关查询
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按用户名查询
     */
    Optional<User> findByUsername(String username);

    /**
     * 用户名是否存在
     */
    Boolean existsByUsername(String username);

    /**
     * 按用户名与密码查询（登录验证）
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password
    );

    /**
     * 按昵称模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname%")
    Optional<User> findByNicknameContaining(@Param("nickname") String nickname);

    /**
     * 统计相同昵称的用户数
     */
    Long countByNickname(String nickname);

    /**
     * 按用户 ID 与用户名查询
     */
    Optional<User> findByIdAndUsername(Long id, String username);
}