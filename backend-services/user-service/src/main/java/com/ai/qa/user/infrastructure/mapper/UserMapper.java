package com.ai.qa.user.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.UserInfo;
import com.ai.qa.user.application.dto.SaveRegisterCommand;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.infrastructure.entity.UserPO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserPO userpo);
    UserPO toUserOP(User user);  
    SaveRegisterCommand toCommand(RegisterRequest registerRequest);
    UserInfo toUserInfo(User user);
}
