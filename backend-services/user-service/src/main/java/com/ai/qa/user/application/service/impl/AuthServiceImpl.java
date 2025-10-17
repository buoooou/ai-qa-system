package com.ai.qa.user.application.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.application.service.AuthService;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.mapper.UserMapper;
import com.ai.qa.user.domain.repository.UserRepo;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Operation(summary = "用户鉴权")
    @Override
    public LoginResponse authenticate(String username, String password) throws AuthenticationException {
        // log.debug("[User-Service] [{}]## Method {} Start.", this.getClass().getSimpleName(), "authenticate");
        // log.debug("[User-Service] [{}]## username:{}, password:{}", this.getClass().getSimpleName(), "authenticate", username, password);

        // Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // log.debug("[User-Service] [{}]## Print UserDetails. UserDetails.username:{}, UserDetails.password:{}", this.getClass().getSimpleName(), "authenticate", userDetails.getUsername(), userDetails.getPassword());

        // UserResponse userDto = userRepository.findByUsername(username)
        //         .map(x -> userMapper.userToUserDTO(x))
        //         .orElseThrow(() -> new EntityNotFoundException());

        // log.info("[User-Service] [{}]## 用户认证通过。用户ID:{}，用户名:{}", this.getClass().getSimpleName(), "authenticate", userDto.getId(), username);

        // return LoginResponse.builder().token(jwtUtil.generateToken(authentication.getName())).userId(userDto.getId()).build();
        return null;
    }

}
