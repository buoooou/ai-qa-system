package com.ai.qa.user.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.user.api.dto.UserRegisterDTO;
import com.ai.qa.user.api.dto.UserResponseDTO;
import com.ai.qa.user.domain.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDTO toDto(User user);

    User toEntity(UserRegisterDTO dto);
}
