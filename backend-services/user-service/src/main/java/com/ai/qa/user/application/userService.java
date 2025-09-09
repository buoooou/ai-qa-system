package com.ai.qa.user.application;

import com.ai.qa.user.domain.entity.User;

public interface UserService {
   User login(String username,String pwd);

   User register(String username,String pwd, String nick);

   User findByUsername(String username);

   int updateNick(String nick, Long userId);
}
