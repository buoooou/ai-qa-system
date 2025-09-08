package com.ai.qa.user.application;

import com.ai.qa.user.domain.entity.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class userServiceImpl implements userService {
    
    @Override
    public User login(String username, String pwd) {
        // 实际项目中需要实现认证逻辑
        // 这里简化处理，直接返回一个用户对象
        User user = new User();
        user.setUsername(username);
        return user;
    }

    @Override
    @Transactional
    public User register(String username, String pwd) {
        // 实际项目中需要实现注册逻辑
        // 包括密码加密、保存到数据库等
        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd); // 实际应该加密存储
        // 模拟保存到数据库
        user.setId(1L);
        return user;
    }
    
    @Override
    @Transactional
    public boolean updateNick(String nick, String userId) {
        // 实际项目中需要实现更新用户昵称逻辑
        return true;
    }
}