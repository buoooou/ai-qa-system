package com.ai.qa.user.application.impl;

import com.ai.qa.user.application.UserService;
import com.ai.qa.user.common.CommonUtil;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User login(String username, String pwd) {        
        User user = userRepository.findByUsername(username);
        String encryptedPassword = CommonUtil.encryptPassword(pwd);
        if (user != null && user.getPassword().equals(encryptedPassword)) {
            return user;
        }
        return null;
    }

    @Transactional
    @Override
    public User register(String username, String pwd) {
        return register(username, pwd, null);
    }
    
    @Transactional
    @Override
    public User register(String username, String pwd, String nick) {
        
        // 检查用户是否已存在
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            return null; // 用户已存在
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(CommonUtil.encryptPassword(pwd)); 
        user.setNick(nick);
        
        // 保存到数据库
        User savedUser = userRepository.save(user);
        return savedUser;
    }
    
    @Transactional
    @Override
    public boolean updateNick(String nick, Long userId) {        
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setNick(nick);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
    
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public boolean isNickExists(String nick) {
        User user = userRepository.findByNick(nick);
        return user != null;
    }
}