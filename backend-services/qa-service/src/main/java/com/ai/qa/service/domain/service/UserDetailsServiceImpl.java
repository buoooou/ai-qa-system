package com.ai.qa.service.domain.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ai.qa.service.api.exception.ResourceNotFoundException;
import com.ai.qa.service.infrastructure.persistence.entities.UserPO;
import com.ai.qa.service.infrastructure.persistence.repositories.JpaUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPO userpo = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + username));
        return new org.springframework.security.core.userdetails.User(userpo.getUsername(), userpo.getPassword(), new ArrayList<>());
    }
}
