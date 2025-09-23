package com.ai.qa.user.domain.service;

import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.domain.repositories.UserRepositoryRepo;
import com.ai.qa.user.infrastructure.persistence.repositories.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepositoryRepo userRepositoryRepo;
    private final PasswordEncoder passwordEncoder;

    public boolean validateCredentials(String username, String password) {
        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            return userDetails != null && passwordEncoder.matches(password, userDetails.getPassword());
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    public UserDto registerUser(UserDto userDto) {
        return userRepositoryRepo.register(userDto);
    }

    public UserDto findByUserId(Long userId) {
        return userRepositoryRepo.findByUserId(userId);
    }

    public boolean updateNick(String nickname, String username) {
        int result = userRepositoryRepo.updateNick(nickname, username);
        return result == 1;
    }
}