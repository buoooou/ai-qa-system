package com.ai.qa.user.application.mapper;

import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.domain.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T21:10:34+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public AuthResponse toAuthResponse(User user) {
        if ( user == null ) {
            return null;
        }

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();

        authResponse.profile( new AuthResponse.UserProfile(user.getId(), user.getUsername(), user.getEmail(), user.getNickname(), user.getRole().name()) );

        return authResponse.build();
    }
}
