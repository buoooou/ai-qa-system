package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.UserDto;

public interface UserRepositoryRepo {
    UserDto login(String username, String pwd);
    UserDto findByUserId(Long userId);
    UserDto findByUsername(String username);
    UserDto register(String username, String pwd, String nick);
    int updateNick(String nick, Long userId);
}
