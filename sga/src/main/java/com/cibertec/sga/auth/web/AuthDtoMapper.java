package com.cibertec.sga.auth.web;

import com.cibertec.sga.auth.application.AuthSession;
import com.cibertec.sga.auth.web.dto.AccessTokenResponse;
import com.cibertec.sga.auth.web.dto.UserProfileResponse;
import com.cibertec.sga.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthDtoMapper {

    public UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
            user.getUuid(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getRoleName()
        );
    }

    public AccessTokenResponse toAccessTokenResponse(AuthSession session) {
        return new AccessTokenResponse(
            session.accessToken(), "Bearer", session.accessTokenExpiresInSeconds(), toProfileResponse(session.user())
        );
    }
}
