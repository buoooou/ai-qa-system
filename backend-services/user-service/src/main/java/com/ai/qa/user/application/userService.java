package com.ai.qa.user.application;

import com.ai.qa.user.domain.entity.User;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class userService {
    
    // 模拟数据库存储
    private static final ConcurrentHashMap<String, User> userDatabase = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    public User login(String username, String pwd) {
        // 实际项目中需要实现认证逻辑
        // 这里简化处理，直接返回一个用户对象
        User user = userDatabase.get(username);
        if (user != null && user.getPassword().equals(pwd)) {
            return user;
        }
        return null;
    }

    @Transactional
    public User register(String username, String pwd) {
        // 实际项目中需要实现注册逻辑
        // 包括密码加密、保存到数据库等
        if (userDatabase.containsKey(username)) {
            return null; // 用户已存在
        }
        
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(username);
        user.setPassword(pwd); // 实际应该加密存储
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 模拟保存到数据库
        userDatabase.put(username, user);
        return user;
    }
    
    @Transactional
    public boolean updateNick(String nick, String userId) {
        // 实际项目中需要实现更新用户昵称逻辑
        // 查找用户并更新昵称
        for (User user : userDatabase.values()) {
            if (user.getId().toString().equals(userId)) {
                user.setNick(nick);
                user.setUpdateTime(LocalDateTime.now());
                return true;
            }
        }
        return false;
    }
}