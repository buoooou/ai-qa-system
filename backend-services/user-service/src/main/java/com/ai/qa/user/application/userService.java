package com.ai.qa.user.application;

import com.ai.qa.user.domain.entity.User;

public interface userService {

    User login(String username, String pwd);

    User register(String username, String pwd);
    
    boolean updateNick(String nick, String userId);
}