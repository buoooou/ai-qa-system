package com.ai.qa.user.application;

import com.ai.qa.user.domain.entity.User;
import java.util.List;

public interface UserService {
    
    User login(String username, String pwd);
    
    User register(String username, String pwd);
    
    User register(String username, String pwd, String nick);
    
    boolean updateNick(String nick, Long userId);
    
    User getUserById(Long userId);
    
    List<User> getUsers();
    
    boolean isNickExists(String nick);
}