package com.ai.qa.user.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // @Mapping(source="User", target="UserDTO")
    UserResponse toUserDTO(User user);

    // @Mapping(source="UserDTO", target="User")
    User toUser(UserResponse userDto);

}
