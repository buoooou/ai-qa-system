package com.ai.qa.user.application.query;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    Optional<User> findById(Long userId);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 查询相似用户
     * @param query 查询条件
     * @return 相似用户列表
     */
    List<User> findSimilarUsers(UserQuery query);
    
    /**
     * 获取用户向量嵌入
     * @param userId 用户ID
     * @return 向量嵌入
     */
    Optional<UserVectorEmbedding> getUserVectorEmbedding(Long userId);
}