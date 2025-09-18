package com.ai.qa.user.application.service;

import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.domain.repositories.UserRepositoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryRepo userRepositoryRepo;

    public UserDto login(String username, String pwd) {
        return userRepositoryRepo.login(username, pwd);
    }

    public UserDto register(String username, String pwd, String nick) {
        return userRepositoryRepo.register(username, pwd, nick);
    }

    public UserDto findByUserID(Long userId) {
        UserDto userDto = userRepositoryRepo.findByUserId(userId);
        return userDto;
    }



    public UserDto findByUsername(String username) {
        UserDto userDto = userRepositoryRepo.findByUsername(username);

        if (userDto != null) {
            return userDto;
        } else {
            throw new RuntimeException("该用户不存在");
        }
    }

    public int updateNick(String nick, Long userId) {
        return userRepositoryRepo.updateNick(nick, userId);
    }


}
