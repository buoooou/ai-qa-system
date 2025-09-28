package com.ai.qa.user.application.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.user.api.dto.response.LoginRsponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.UserInfo;
import com.ai.qa.user.api.exception.ResourceNotFoundException;
import com.ai.qa.user.domain.service.JwtServiceImpl;
import com.ai.qa.user.infrastructure.mapper.UserMapper;
import com.ai.qa.user.application.dto.SaveRegisterCommand;
import com.ai.qa.user.application.service.UserCaseService;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceCaseImpl implements UserCaseService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;
    private final UserMapper userMapper;

    @Override
    public LoginRsponse login(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + username));
        LoginRsponse loginRsponse = new LoginRsponse();
        loginRsponse.setToken(jwtServiceImpl.generateToken(user.getUsername()));
        loginRsponse.setUser(userMapper.toUserInfo(user));
        return loginRsponse;
    }

    @Override
    public RegisterResponse register(SaveRegisterCommand command) {
        try {
            User user = User.createNew(command.getUsername(),
                    passwordEncoder.encode(command.getPassword()), command.getEmail(), command.getAvatar());
            userRepository.save(user);
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setToken(jwtServiceImpl.generateToken(user.getUsername()));
            registerResponse.setUser(userMapper.toUserInfo(user));
            return registerResponse;
        } catch (Exception e) {
            throw new ResourceNotFoundException("创建用户失败。");
        }
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
    public String getUserNamebyId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶ID: " + userId));
        return user.getUsername();
    }
    
    @Override
    public UserInfo getUserName(String token) {
    	
        User user = userRepository.findByUsername(jwtServiceImpl.extractUsername(token))
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名: " + jwtServiceImpl.extractUsername(token)));
        return userMapper.toUserInfo(user);
    }
}
