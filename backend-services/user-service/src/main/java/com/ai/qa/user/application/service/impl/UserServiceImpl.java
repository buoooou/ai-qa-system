package com.ai.qa.user.application.service.impl;

import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.application.dto.UserDTO;
import com.ai.qa.user.application.dto.UserCommand;
import com.ai.qa.user.application.mapper.UserAppMapper;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAppMapper userAppMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDTO registerUser(RegisterRequest request) {
        // 1. 构建应用层命令对象
        UserCommand command = new UserCommand();
        command.setUsername(request.getUsername());
        command.setPassword(passwordEncoder.encode(request.getPassword()));

        // 2. 转换为领域对象
        User user = userAppMapper.toDomain(command);

        // 3. 保存领域对象
        User savedUser = userRepository.save(user);

        // 4. 转换为应用DTO返回
        return userAppMapper.toDTO(savedUser);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateNickname(String username, String newNickname) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        user.updateNickname(newNickname);
        userRepository.save(user);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "用户不存在（ID: " + userId + "）"
                ));
        return userAppMapper.toDTO(user);
    }
}