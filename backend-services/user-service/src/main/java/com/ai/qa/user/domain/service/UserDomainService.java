package com.ai.qa.user.domain.service;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;

import java.util.List;

public interface UserDomainService {
    
    /**
     * 验证用户密码
     * @param user 用户实体
     * @param rawPassword 原始密码
     * @return 是否验证成功
     */
    boolean validatePassword(User user, String rawPassword);
    
    /**
     * 生成用户向量嵌入
     * @param user 用户实体
     * @return 用户向量嵌入
     */
    UserVectorEmbedding generateVectorEmbedding(User user);
    
    /**
     * 查找相似用户
     * @param user 用户实体
     * @param limit 限制数量
     * @return 相似用户列表
     */
    List<User> findSimilarUsers(User user, int limit);
    
    /**
     * 检查用户名是否有效
     * @param username 用户名
     * @return 是否有效
     */
    boolean isValidUsername(String username);
    
    /**
     * 检查密码强度
     * @param password 密码
     * @return 是否符合强度要求
     */
    boolean isStrongPassword(String password);
}