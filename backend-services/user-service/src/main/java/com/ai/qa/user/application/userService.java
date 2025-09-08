package com.ai.qa.user.application;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class userService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User login(String username, String pwd) {
        // 实际项目中需要实现认证逻辑
        // 这里简化处理，直接返回一个用户对象
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(pwd)) {
            return user;
        }
        return null;
    }

    @Transactional
    public User register(String username, String pwd) {
        // 实际项目中需要实现注册逻辑
        // 包括密码加密、保存到数据库等
        // 检查用户是否已存在
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            return null; // 用户已存在
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd); // 实际应该加密存储
        
        // 保存到数据库
        User savedUser = userRepository.save(user);
        return savedUser;
    }
    
    @Transactional
    public boolean updateNick(String nick, Long userId) {
        // 实际项目中需要实现更新用户昵称逻辑
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setNick(nick);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
    
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}