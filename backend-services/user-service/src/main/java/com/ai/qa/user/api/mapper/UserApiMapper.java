package com.ai.qa.user.api.mapper;

import com.ai.qa.user.api.dto.response.UserResponse;
import com.ai.qa.user.application.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserApiMapper {
    UserResponse toResponse(UserDTO dto);
}