package com.ai.qa.user.domain.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ai.qa.user.infrastructure.entity.UserPO;
import com.ai.qa.user.infrastructure.repository.JpaUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPO userpo = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(userpo.getUsername(), userpo.getPassword(), new ArrayList<>());
    }
}
