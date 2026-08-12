package com.cibertec.sga.refreshtoken.infrastructure.persistence;

import com.cibertec.sga.refreshtoken.domain.model.RefreshToken;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshToken toDomain(RefreshTokenEntity entity, UUID userUuid) {
        return RefreshToken.builder()
            .uuid(entity.getUuid())
            .userUuid(userUuid)
            .tokenHash(entity.getTokenHash())
            .expiresAt(entity.getExpiresAt())
            .revokedAt(entity.getRevokedAt())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public RefreshTokenEntity toNewEntity(RefreshToken refreshToken, Long userId) {
        return RefreshTokenEntity.builder()
            .userId(userId)
            .tokenHash(refreshToken.getTokenHash())
            .expiresAt(refreshToken.getExpiresAt())
            .build();
    }
}
