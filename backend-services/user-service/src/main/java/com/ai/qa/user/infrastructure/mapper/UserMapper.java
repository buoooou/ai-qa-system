package com.ai.qa.user.infrastructure.mapper;

import com.ai.qa.user.api.dto.RegisterReqDto;
import com.ai.qa.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // 交给 Service 处理
    @Mapping(target = "createTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    User toUser(RegisterReqDto dto);
}