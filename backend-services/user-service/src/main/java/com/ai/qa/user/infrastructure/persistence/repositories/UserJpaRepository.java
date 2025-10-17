package com.ai.qa.user.infrastructure.persistence.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repository.UserRepo;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepo {
    // Spring Data JPA 会自动实现 JpaRepository 中的所有方法，
    // 包括我们UserRepository中定义的 findById 和 save 方法，因为它们的方法签名是匹配的。
    // 因此，这里不需要写任何代码。

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    /**
     * 根据用户名和密码查询用户
     * 用于登录验证，同时匹配用户名和加密后的密码
     *
     * @param username 用户名
     * @param password 加密后的密码
     * @return Optional<User> 用户信息的Optional包装
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password
    );

    /**
     * 统计指定昵称的用户数量
     * 用于验证昵称的唯一性或统计使用情况
     *
     * @param nickname 昵称
     * @return Long 用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.nickname = :nickname")
    Long countNickname(@Param("nickname") String nickname);
}
