package com.ai.qa.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.dto.LoginDto;
import com.ai.qa.user.application.dto.Response;
import com.ai.qa.user.common.CommonUtil;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.domain.repositories.UserRepositoryRepo;
import com.ai.qa.user.domain.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryRepo userRepositoryRepo;

    public LoginDto login(String username, String password) {

        if (authService.validateCredentials(username, password)) {
            UserDto userDto = userRepositoryRepo.findByUsername(username);
            LoginDto loginDto = new LoginDto();
            loginDto.setUserid(userDto.getId() != null ? userDto.getId().toString() : null);
            loginDto.setUsername(userDto.getUsername());
            loginDto.setNickname(userDto.getNickname());
            loginDto.setToken(jwtUtil.generateToken(userDto.getUsername()));
            // loginDto.setSessionId(java.util.UUID.randomUUID().toString());
            return loginDto;
        }
        return null;
    }

    public UserDto register(String username, String password, String nickname) {

        String encodedPassword = passwordEncoder.encode(password);
        String nick = nickname;
        if (nick == null || nick.length() == 0) {
            nick = CommonUtil.generateDefaultNick();
        }
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setPassword(encodedPassword);
        userDto.setNickname(nick);

        userDto = authService.registerUser(userDto);
        return userDto;
    }

    public Response<?> findByUserID(Long userId) {
        try {
            UserDto userDto = authService.findByUserId(userId);
            return Response.success(userDto != null ? "exist" : "not exist");
        } catch (Exception e) {
            return Response.error(ErrCode.INTERNAL_SERVER_ERROR.getCode(), ErrCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    public UserDto findByUsername(String username) {
        UserDto userDto = userRepositoryRepo.findByUsername(username);
        return userDto;
    }

    public boolean updateNick(String nickname, String username) {
        return authService.updateNick(nickname, username);
    }


}
