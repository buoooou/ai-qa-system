package com.ai.qa.user.domain.repository;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserName(String userName);
    
    boolean existsByUserName(String userName);
    
    /**
     * 根据向量嵌入查找相似用户
     * @param vectorEmbedding 向量嵌入
     * @param limit 限制数量
     * @return 相似用户列表
     */
    List<User> findSimilarUsersByVector(UserVectorEmbedding vectorEmbedding, int limit);
    
    /**
     * 保存用户向量嵌入
     * @param userId 用户ID
     * @param vectorEmbedding 向量嵌入
     */
    void saveVectorEmbedding(Long userId, UserVectorEmbedding vectorEmbedding);
    
    /**
     * 获取用户向量嵌入
     * @param userId 用户ID
     * @return 向量嵌入
     */
    Optional<UserVectorEmbedding> findVectorEmbeddingByUserId(Long userId);
    
    default User insertUser(User user) {
        return save(user);
    }
}
