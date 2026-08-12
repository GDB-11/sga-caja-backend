package com.cibertec.sga.refreshtoken.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"RefreshToken\" WHERE \"TokenHash\" = :tokenHash")
    Optional<RefreshTokenEntity> findByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(nativeQuery = true,
        value = "UPDATE \"RefreshToken\" SET \"RevokedAt\" = now() WHERE \"TokenHash\" = :tokenHash")
    void revokeByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(nativeQuery = true,
        value = "UPDATE \"RefreshToken\" SET \"RevokedAt\" = now() "
            + "WHERE \"UserId\" = :userId AND \"RevokedAt\" IS NULL")
    void revokeAllActiveByUserId(@Param("userId") Long userId);
}
