package com.ai.qa.user.infrastructure.adapter;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class VectorStoreAdapter {
    
    /**
     * 生成文本的向量嵌入（简化版本，使用随机向量）
     * @param text 文本内容
     * @return 向量嵌入
     */
    public UserVectorEmbedding generateEmbedding(String text) {
        // 在实际应用中，这里应该使用真正的嵌入模型
        // 现在为了演示，我们生成一个基于文本哈希的伪随机向量
        int dimension = 1536; // OpenAI text-embedding-ada-002 的维度
        float[] embedding = new float[dimension];
        
        // 使用文本的哈希值作为随机种子
        int seed = text.hashCode();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] = random.nextFloat(-1.0f, 1.0f);
        }
        
        return new UserVectorEmbedding(embedding, "mock-embedding-model", dimension);
    }
    
    /**
     * 保存用户向量到向量数据库（简化版本）
     * @param user 用户信息
     * @param vectorEmbedding 向量嵌入
     */
    public void saveUserVector(User user, UserVectorEmbedding vectorEmbedding) {
        // 在实际应用中，这里应该保存到真正的向量数据库
        // 现在为了演示，我们只是记录日志
        log.info("Saving vector for user: {}, username: {}, nickname: {}", 
                user.getId(), user.getUserName(), user.getNickName());
        
        // 更新用户实体的向量嵌入
        user.updateVectorEmbedding(vectorEmbedding);
    }
    
    /**
     * 查找相似用户（简化版本）
     * @param user 用户信息
     * @param limit 限制数量
     * @return 相似用户ID列表
     */
    public List<Long> findSimilarUsers(User user, int limit) {
        // 在实际应用中，这里应该查询向量数据库获取相似用户
        // 现在为了演示，我们返回空列表
        log.info("Finding similar users for user: {}, limit: {}", user.getId(), limit);
        return List.of();
    }
    
    /**
     * 计算两个用户之间的相似度
     * @param user1 用户1
     * @param user2 用户2
     * @return 相似度分数
     */
    public double calculateSimilarity(User user1, User user2) {
        if (!user1.hasVectorEmbedding() || !user2.hasVectorEmbedding()) {
            return 0.0;
        }
        
        return user1.getVectorEmbedding().cosineSimilarity(user2.getVectorEmbedding());
    }
}