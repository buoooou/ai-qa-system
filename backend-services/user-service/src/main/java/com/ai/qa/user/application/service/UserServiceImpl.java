package com.ai.qa.user.application.service;

import java.util.Optional;

import com.ai.qa.user.application.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User login(String username, String pwd) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(pwd)) {
            return userOptional.get();
        }
        return null;
    }

    @Override
    public User register(String username, String pwd, String nick) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd);
        user.setNick(nick); // 默认昵称为用户名
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new RuntimeException("该用户不存在");
        }
    }

    @Override
    public int updateNick(String nick, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent() && nick != null && !nick.isEmpty()) {
            User user = userOptional.get();
            user.setNick(nick);
            userRepository.save(user);
            return 1;
        }
        return 0;
    }


}
