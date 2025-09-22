package com.ai.qa.user.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.user.api.dto.UserDTO;
import com.ai.qa.user.domain.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // @Mapping(source="User", target="UserDTO")
    UserDTO userToUserDTO(User user);

    // @Mapping(source="UserDTO", target="User")
    User userDTOToUser(UserDTO userDto);

    

}
