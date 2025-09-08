package com.ai.qa.user.infrastructure.repository;

import com.ai.qa.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 * 
 * 继承JpaRepository，提供基本的CRUD操作
 * 定义用户相关的数据库查询方法
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * 
     * @param userName 用户名
     * @return Optional<User> 用户信息
     */
    Optional<User> findByUserName(String userName);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱地址
     * @return Optional<User> 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     * 
     * @param userName 用户名
     * @return boolean true-存在，false-不存在
     */
    boolean existsByUserName(String userName);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱地址
     * @return boolean true-存在，false-不存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据用户名和状态查找用户
     * 
     * @param userName 用户名
     * @param status 用户状态（1-启用，0-禁用）
     * @return Optional<User> 用户信息
     */
    Optional<User> findByUserNameAndStatus(String userName, Integer status);
    
    /**
     * 根据邮箱和状态查找用户
     * 
     * @param email 邮箱地址
     * @param status 用户状态（1-启用，0-禁用）
     * @return Optional<User> 用户信息
     */
    Optional<User> findByEmailAndStatus(String email, Integer status);
    
    /**
     * 统计启用的用户数量
     * 
     * @return long 启用用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    long countEnabledUsers();
    
    /**
     * 统计禁用的用户数量
     * 
     * @return long 禁用用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 0")
    long countDisabledUsers();
    
    /**
     * 根据用户名模糊查询用户
     * 
     * @param userName 用户名关键词
     * @return java.util.List<User> 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.userName LIKE %:userName%")
    java.util.List<User> findByUserNameContaining(@Param("userName") String userName);
    
    /**
     * 根据邮箱模糊查询用户
     * 
     * @param email 邮箱关键词
     * @return java.util.List<User> 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
    java.util.List<User> findByEmailContaining(@Param("email") String email);
    
    /**
     * 查找最近注册的用户
     * 
     * @param pageable 分页参数
     * @return java.util.List<User> 用户列表
     */
    @Query("SELECT u FROM User u ORDER BY u.createTime DESC")
    java.util.List<User> findRecentUsers(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据创建时间范围查询用户
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return java.util.List<User> 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.createTime BETWEEN :startTime AND :endTime")
    java.util.List<User> findByCreateTimeBetween(
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime
    );
    
    /**
     * 批量更新用户状态
     * 
     * @param userIds 用户ID列表
     * @param status 用户状态（1-启用，0-禁用）
     * @return int 更新的记录数
     */
    @Query("UPDATE User u SET u.status = :status, u.updateTime = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int updateUserStatusBatch(@Param("userIds") java.util.List<Long> userIds, @Param("status") Integer status);
    
    /**
     * 删除指定时间之前创建的禁用用户
     * 
     * @param beforeTime 时间点
     * @return int 删除的记录数
     */
    @Query("DELETE FROM User u WHERE u.status = 0 AND u.createTime < :beforeTime")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int deleteDisabledUsersBefore(@Param("beforeTime") java.time.LocalDateTime beforeTime);
}
