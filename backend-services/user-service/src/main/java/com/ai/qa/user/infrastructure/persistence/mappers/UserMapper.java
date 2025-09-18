package com.ai.qa.user.infrastructure.persistence.mappers;

import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.infrastructure.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper interface for converting between {@link User} entity and {@link UserDto} DTO.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserDto} DTO.
     *
     * @param user the user entity to convert
     * @return the converted user DTO
     */
    UserDto toUserDto(User user);

    /**
     * Converts a {@link UserDto} DTO to a {@link User} entity.
     *
     * @param userDto the user DTO to convert
     * @return the converted user entity
     */
    User toUser(UserDto userDto);
}