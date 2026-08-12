package com.cibertec.sga.refreshtoken.infrastructure.persistence;

import com.cibertec.sga.refreshtoken.domain.model.RefreshToken;
import com.cibertec.sga.refreshtoken.domain.repository.IRefreshTokenRepository;
import com.cibertec.sga.user.infrastructure.persistence.UserEntity;
import com.cibertec.sga.user.infrastructure.persistence.UserJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository implements IRefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenMapper mapper;

    public RefreshTokenRepository(
        RefreshTokenJpaRepository jpaRepository, UserJpaRepository userJpaRepository, RefreshTokenMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(entity -> {
            UserEntity user = userJpaRepository.findById(entity.getUserId()).orElseThrow();
            return mapper.toDomain(entity, user.getUuid());
        });
    }

    @Override
    public RefreshToken insert(RefreshToken refreshToken) {
        UserEntity user = userJpaRepository.findByUuid(refreshToken.getUserUuid()).orElseThrow();
        RefreshTokenEntity entity = mapper.toNewEntity(refreshToken, user.getId());
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved, user.getUuid());
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        jpaRepository.revokeByTokenHash(tokenHash);
    }

    @Override
    public void revokeAllActiveByUserUuid(UUID userUuid) {
        UserEntity user = userJpaRepository.findByUuid(userUuid).orElseThrow();
        jpaRepository.revokeAllActiveByUserId(user.getId());
    }
}
