package com.ai.qa.user.application.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ai.qa.user.api.exception.ResourceNotFoundException;
import com.ai.qa.user.domain.service.JwtServiceImpl;
import com.ai.qa.user.application.dto.SaveRegisterCommand;
import com.ai.qa.user.application.service.UserCaseService;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceCaseImpl implements UserCaseService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;

    @Override
    public String login(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + username));
        String token = jwtServiceImpl.generateToken(user.getUsername());
        return token;
    }

    @Override
    public boolean register(SaveRegisterCommand command) {
        try {
            User user = User.createNew(command.getUsername(),
                    passwordEncoder.encode(command.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceNotFoundException("创建用户失败。");
        }
        return true;
    }

    @Override
    public boolean updateNick(String username, String updatename) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + username));
        try {
            user.updateNick(updatename);
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceNotFoundException("用户更新失败。");
        }
        return true;
    }

    @Override
    public String getUserName(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶ID: " + userId));
        return user.getUsername();
    }
}
