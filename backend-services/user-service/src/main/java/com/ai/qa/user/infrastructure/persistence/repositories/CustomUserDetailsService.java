package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.infrastructure.persistence.entities.User;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOptional = jpaUserRepository.findByUsername(username);
        if( userOptional.isPresent()) {
            User user = userOptional.get();
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("user")
                .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

    }
}