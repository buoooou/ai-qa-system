package com.ai.qa.user.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.domain.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(RegisterRequest registerRequest);
    LoginRequest toLogin(User user);
}
