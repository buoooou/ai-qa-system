package com.ai.qa.user.domain.service;

import com.ai.qa.user.api.dto.LoginReqDto;
import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.api.dto.UpdateNicknameReqDto;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.infrastructure.adapter.PasswordEncoderAdapter;
import com.ai.qa.user.infrastructure.mapper.UserMapper;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoderAdapter passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoderAdapter passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 注册用户
    public User registerUser(RegisterReqDto request) {
        // 用户名唯一性校验
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(ErrCode.USERNAME_EXISTS);
        }
        // 参数校验
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new BusinessException(ErrCode.USERNAME_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BusinessException(ErrCode.PASSWORD_REQUIRED);
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 使用 MapStruct进行转换
        User user = UserMapper.INSTANCE.toUser(request, encodedPassword);
        System.out.println("=================================="+user.getUsername());
        return userRepository.save(user);

    }

    // 用户登录
    public User loginUser(LoginReqDto request) {
        // 用户名存在性校验
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .orElseThrow(() -> new BusinessException(ErrCode.INVALID_CREDENTIALS));
    }

    // 更新用户昵称
    public void updateNickname(UpdateNicknameReqDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrCode.USERNAME_NOT_FOUND));
        user.setNickname(request.getNickname());
        userRepository.save(user);
    }

}