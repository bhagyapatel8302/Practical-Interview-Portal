package com.tatvasoft.interview_portal.mapper;

import com.tatvasoft.interview_portal.dto.UserRequest;
import com.tatvasoft.interview_portal.dto.UserResponse;
import com.tatvasoft.interview_portal.entity.Role;
import com.tatvasoft.interview_portal.entity.User;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-08T17:05:54+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRequest request, String encodedPassword, Role role, Long currentUserId) {
        if ( request == null && encodedPassword == null && role == null && currentUserId == null ) {
            return null;
        }

        User user = new User();

        if ( request != null ) {
            user.setUsername( request.getUsername() );
            user.setEmail( request.getEmail() );
            user.setIsActive( request.getIsActive() );
        }
        if ( role != null ) {
            user.setId( role.getId() );
        }
        user.setPassword( encodedPassword );
        user.setCreatedBy( currentUserId );
        user.setCreatedAt( LocalDateTime.now() );

        return user;
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Long roleId = null;
        Long id = null;
        String username = null;
        String email = null;
        Boolean isActive = null;

        roleId = userRoleId( user );
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        isActive = user.getIsActive();

        UserResponse userResponse = new UserResponse( id, username, email, roleId, isActive );

        return userResponse;
    }

    private Long userRoleId(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        Long id = role.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
