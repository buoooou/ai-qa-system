package com.ai.qa.user.application.service.impl;


import java.time.LocalDateTime;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.user.api.dto.UserRequestDto;
import com.ai.qa.user.api.exception.BussinessException;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.entity.User;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;
    
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//      }
    
    public User login(UserRequestDto loginRequest){
  	   log.info("开始处理登录请求");
       // 1. 检查用户名是否已存在
       Optional<User> existingUser = userRepository.findByUsername(loginRequest.getUsername());
       if (existingUser.isEmpty()) {
           throw new BussinessException(HttpStatus.BAD_REQUEST,"用户不存在");
       }
   	   log.info("{}登录成功",loginRequest.getUsername());
   	   
        User user  = new User();
        user.setUsername(existingUser.get().getUsername());
        user.setPassword(existingUser.get().getPassword());
        user.setId(existingUser.get().getId());
        user.setRole(existingUser.get().getRole());
        user.setNickname(existingUser.get().getNickname());
		return user;
            
    };

    /**
     * 注册逻辑实现
     */
    @Override
    @Transactional
    public User register(UserRequestDto registerRequest) {
 	   log.info("开始处理注册请求");
        // 1. 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(registerRequest.getUsername());
        if (!existingUser.isEmpty()) {
        	throw new BussinessException(HttpStatus.BAD_REQUEST,"用户名已存在");
        }

        // 3. 构建用户实体
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        // 4. 密码加密（使用BCrypt加密算法）
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNickname(registerRequest.getNickname());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setRole(registerRequest.getRole());

        // 5. 保存用户信息到数据库
        userRepository.save(user);
  	   log.info("{}注册成功",user.getUsername());
       
		return user;
    }


    /**
     * 更新密码
     */
    public void updatePassword(UserRequestDto userRequestDto){
  	   log.info("开始处理更新密码请求");
       // 1. 检查用户名是否存在
       Optional<User> existingUser = userRepository.findByUsername(userRequestDto.getUsername());
       if (existingUser.isEmpty()) {
           throw new BussinessException(HttpStatus.BAD_REQUEST,"用户不存在");
       }

       existingUser.get().setPassword(userRequestDto.getPassword());
       existingUser.get().setUpdateTime(LocalDateTime.now());
       userRepository.save(existingUser.get());
   	   log.info("{}更新密码成功",existingUser.get().getUsername());
    };



    /**
     * 更新昵称
     */
    public User updateNickName(UserRequestDto userRequestDto){
  	   log.info("开始处理更新昵称请求");
       // 1. 检查用户名是否已存在
       Optional<User> existingUser = userRepository.findByUsername(userRequestDto.getUsername());
       if (existingUser.isEmpty()) {
           throw new BussinessException(HttpStatus.BAD_REQUEST,"用户不存在");
       }
       existingUser.get().setNickname(userRequestDto.getNickname());
       existingUser.get().setUpdateTime(LocalDateTime.now());
       userRepository.save(existingUser.get());
   	   log.info("{}更新昵称成功",existingUser.get().getUsername());
		return existingUser.get();
    };

}
