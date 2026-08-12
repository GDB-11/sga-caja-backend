package com.cibertec.sga.user.infrastructure.persistence;

import com.cibertec.sga.role.infrastructure.persistence.RoleEntity;
import com.cibertec.sga.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity, RoleEntity role) {
        return User.builder()
            .uuid(entity.getUuid())
            .username(entity.getUsername())
            .passwordHash(entity.getPasswordHash())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .roleUuid(role.getUuid())
            .roleName(role.getName())
            .active(entity.isActive())
            .build();
    }
}
