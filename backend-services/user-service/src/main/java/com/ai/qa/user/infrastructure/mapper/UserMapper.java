package com.ai.qa.user.infrastructure.mapper;

import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.domain.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // 自定义映射方法
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    User toUser(RegisterReqDto request, String encodedPassword);

    @AfterMapping
    default void setPassword(@MappingTarget User user, RegisterReqDto request, String encodedPassword) {
        user.setPassword(encodedPassword);
    }
}