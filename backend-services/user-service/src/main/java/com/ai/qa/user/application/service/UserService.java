package com.ai.qa.user.application.service;


import org.springframework.stereotype.Service;

import com.ai.qa.user.api.dto.AuthRequestDTO;
import com.ai.qa.user.api.dto.UserDTO;


@Service
public interface UserService {

    UserDTO getUserByUsername(String username);

    UserDTO updateNickname(String username, String newNickname);

    UserDTO register(AuthRequestDTO request);
}
