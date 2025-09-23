package com.ai.qa.user.infrastructure.persistence.mappers;

import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.infrastructure.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper interface for converting between {@link User} entity and {@link UserDto} DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserDto} DTO.
     *
     * @param user the user entity to convert
     * @return the converted user DTO
     */
    @Mapping(source = "id", target = "userid")
    UserDto toUserDto(User user);

    /**
     * Converts a {@link UserDto} DTO to a {@link User} entity.
     *
     * @param userDto the user DTO to convert
     * @return the converted user entity
     */
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "nickname", target = "nickname")
    User toUser(UserDto userDto);
}