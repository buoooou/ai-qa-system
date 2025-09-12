package com.ai.qa.user.application.impl;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO register(UserRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    @Override
    public String login(UserLoginDTO dto) {
        Optional<User> optional = userRepository.findByUsername(dto.getUsername());
        if (optional.isEmpty()) {
            throw new RuntimeException("用户名不存在");
        }
        User user = optional.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 这里返回一个简单示例 token
        return "jwt-token-for-user-" + user.getId();
    }

    @Override
    public UserResponseDTO updateNickname(Long id, String nickname) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setNickname(nickname);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }
}
