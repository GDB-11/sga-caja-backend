package com.cibertec.sga.role.infrastructure.persistence;

import com.cibertec.sga.role.domain.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toDomain(RoleEntity entity) {
        return Role.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .build();
    }
}
