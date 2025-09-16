package com.ai.qa.user.application.mapper;

import com.ai.qa.user.application.dto.UserDTO;
import com.ai.qa.user.application.dto.UserCommand;
import com.ai.qa.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAppMapper {

    // 领域对象 -> 应用DTO
    UserDTO toDTO(User user);

    // 应用命令 -> 领域对象
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    User toDomain(UserCommand command);
}