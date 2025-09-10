package com.ai.qa.user.application.impl;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdateNickRequest;
import com.ai.qa.user.api.exception.ResourceNotFoundException;
import com.ai.qa.user.api.exception.InvalidCredentialsException;
import com.ai.qa.user.application.service.JwtService;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.domain.Repository.UserRepository;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.persistence.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
	private final JwtService jwtService;
    
    @Override
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + loginRequest.getUsername()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}
		String token = jwtService.generateToken(user.getUsername());
		return token;
    }

    @Override
    public boolean register(RegisterRequest registerRequest) {
        try {
            User user = userMapper.toUser(registerRequest);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateNick(String username, UpdateNickRequest updateNickRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + username));
        try {
            user.setUsername(updateNickRequest.getUsername());
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
        } catch(Exception e) {
            return false;
        }
        return true;
    }
}
