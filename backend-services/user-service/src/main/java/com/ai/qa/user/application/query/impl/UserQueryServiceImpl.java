package com.ai.qa.user.application.query.impl;

import com.ai.qa.user.application.query.UserQuery;
import com.ai.qa.user.application.query.UserQueryService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;
import com.ai.qa.user.infrastructure.adapter.VectorStoreAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    
    private final UserRepository userRepository;
    private final VectorStoreAdapter vectorStoreAdapter;
    
    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserName(username);
    }
    
    @Override
    public List<User> findSimilarUsers(UserQuery query) {
        if (query.getUserId() == null) {
            return List.of();
        }
        
        Optional<User> userOpt = userRepository.findById(query.getUserId());
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        User user = userOpt.get();
        int limit = query.getLimit() != null ? query.getLimit() : 10;
        
        // 使用向量存储适配器查找相似用户
        List<Long> similarUserIds = vectorStoreAdapter.findSimilarUsers(user, limit);
        
        // 获取用户详细信息
        List<User> similarUsers = userRepository.findAllById(similarUserIds);
        
        // 如果设置了相似度阈值，进行过滤
        if (query.getSimilarityThreshold() != null) {
            double threshold = query.getSimilarityThreshold();
            similarUsers = similarUsers.stream()
                    .filter(similarUser -> {
                        double similarity = vectorStoreAdapter.calculateSimilarity(user, similarUser);
                        return similarity >= threshold;
                    })
                    .collect(Collectors.toList());
        }
        
        return similarUsers;
    }
    
    @Override
    public Optional<UserVectorEmbedding> getUserVectorEmbedding(Long userId) {
        return userRepository.findVectorEmbeddingByUserId(userId);
    }
}