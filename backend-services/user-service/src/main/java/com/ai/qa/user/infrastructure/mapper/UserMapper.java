
package com.ai.qa.user.infrastructure.mapper;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.infrastructure.persistence.entities.UserPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserPO toPO(User user);

    User toDomain(UserPO userPO);

}