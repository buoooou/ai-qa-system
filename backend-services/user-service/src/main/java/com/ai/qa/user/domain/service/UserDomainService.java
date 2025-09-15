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
    
    /**
     * 用户注册业务逻辑
     * 包含用户名唯一性检查等跨聚合的业务规则
     */
    public User registerUser(String username, String password, String nickname, 
                           java.util.function.Supplier<Boolean> usernameExistsChecker) {
        // 业务规则：用户名必须唯一
        if (usernameExistsChecker.get()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 使用聚合根的工厂方法创建用户
        return User.createNewUser(username, password, nickname, passwordEncoder);
    }
    
    /**
     * 用户登录业务逻辑
     * 包含密码验证等业务规则
     */
    public User authenticateUser(String username, String password, 
                               java.util.function.Function<String, User> userFinder) {
        User user = userFinder.apply(username);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 使用聚合根的业务方法验证密码
        if (!user.verifyPassword(password, passwordEncoder)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        return user;
    }
    
    /**
     * 密码修改业务逻辑
     * 包含密码修改的所有业务规则
     */
    public void changeUserPassword(User user, String oldPassword, String newPassword) {
        // 使用聚合根的业务方法修改密码
        user.changePassword(oldPassword, newPassword, passwordEncoder);
    }
    
    /**
     * 昵称更新业务逻辑
     */
    public void updateUserNickname(User user, String nickname) {
        // 使用聚合根的业务方法更新昵称
        user.updateNickname(nickname);
    }
}


