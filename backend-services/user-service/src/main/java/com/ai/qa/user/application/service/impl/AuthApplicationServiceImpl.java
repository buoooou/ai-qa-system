package com.ai.qa.user.application.service.impl;

import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrorCode;
import com.ai.qa.user.application.mapper.UserMapper;
import com.ai.qa.user.application.service.AuthApplicationService;
import com.ai.qa.user.common.JwtTokenService;
import com.ai.qa.user.common.PasswordService;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;

    /**
     * Registers a new user and returns a token.
     *
     * @param request registration payload containing username, email, password, nickname
     * @return auth response containing JWT token and profile info
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱已存在");
        }

        String passwordHash = passwordService.encode(request.getPassword());
        User user = User.create(request.getUsername(), request.getEmail(), passwordHash, request.getNickname());
        User saved = userRepository.save(user);

        return buildAuthResponse(saved);
    }

    /**
     * Authenticates user credentials and returns a token.
     *
     * @param usernameOrEmail username or email supplied by user
     * @param rawPassword     plaintext password for validation
     * @return auth response containing JWT token and profile info
     */
    @Override
    public AuthResponse login(String usernameOrEmail, String rawPassword) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordService.matches(rawPassword, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名或密码错误");
        }

        user.markLoginNow();
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtTokenService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        long expiresIn = jwtTokenService.getExpirationSeconds();
        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);
        response.setExpiresIn(expiresIn);
        return response;
    }
}
