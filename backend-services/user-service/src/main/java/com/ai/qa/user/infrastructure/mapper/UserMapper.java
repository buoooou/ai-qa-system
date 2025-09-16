// com/ai/qa/user/infrastructure/mapper/UserMapper.java
package com.ai.qa.user.infrastructure.mapper;

import com.ai.qa.user.api.dto.response.UserResponse;
import com.ai.qa.user.application.dto.UserCommand;
import com.ai.qa.user.application.dto.UserDTO;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.infrastructure.persistence.entities.UserPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

//    @Mapping(target = "createTime", expression = "java(user.getCreateTime() == null ? LocalDateTime.now() : user.getCreateTime())")
//    @Mapping(target = "updateTime", expression = "java(user.getUpdateTime() == null ? LocalDateTime.now() : user.getUpdateTime())")

    UserPO toPO(User user);

    User toDomain(UserPO userPO);

}