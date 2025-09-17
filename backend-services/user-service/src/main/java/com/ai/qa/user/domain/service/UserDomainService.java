package com.ai.qa.user.domain.service;

import com.ai.qa.user.domain.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 用户领域服务 - 处理跨聚合或复杂的业务逻辑
 * 当业务逻辑不能完全归属于单个聚合根时使用
 */
@Component
public class UserDomainService {
    
    private final PasswordEncoder passwordEncoder;
    
    public UserDomainService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public User registerUser(String username, String password, String nickname, 
                           java.util.function.Supplier<Boolean> usernameExistsChecker) {
        if (usernameExistsChecker.get()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        return User.createNewUser(username, password, nickname, passwordEncoder);
    }
    
    public User authenticateUser(String username, String password, 
                               java.util.function.Function<String, User> userFinder) {
        User user = userFinder.apply(username);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        if (!user.verifyPassword(password, passwordEncoder)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        return user;
    }
    
    public void changeUserPassword(User user, String oldPassword, String newPassword) {
        user.changePassword(oldPassword, newPassword, passwordEncoder);
    }
    
    public void updateUserNickname(User user, String nickname) {
        user.updateNickname(nickname);
    }
}


