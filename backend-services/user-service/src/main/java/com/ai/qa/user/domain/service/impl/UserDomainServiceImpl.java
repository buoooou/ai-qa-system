package com.ai.qa.user.domain.service.impl;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.service.UserDomainService;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;
import com.ai.qa.user.infrastructure.adapter.VectorStoreAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VectorStoreAdapter vectorStoreAdapter;
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$");
    
    @Override
    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    
    @Override
    public UserVectorEmbedding generateVectorEmbedding(User user) {
        String userText = String.format("%s %s", user.getUserName(), user.getNickName() != null ? user.getNickName() : "");
        UserVectorEmbedding embedding = vectorStoreAdapter.generateEmbedding(userText);
        
        // 保存到向量数据库
        vectorStoreAdapter.saveUserVector(user, embedding);
        
        // 更新用户实体的向量嵌入
        user.updateVectorEmbedding(embedding);
        
        return embedding;
    }
    
    @Override
    public List<User> findSimilarUsers(User user, int limit) {
        List<Long> similarUserIds = vectorStoreAdapter.findSimilarUsers(user, limit);
        return userRepository.findAllById(similarUserIds);
    }
    
    @Override
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    @Override
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}