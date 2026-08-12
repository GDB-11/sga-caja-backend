package com.cibertec.sga.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"User\" WHERE \"Username\" = :username")
    Optional<UserEntity> findByUsername(@Param("username") String username);

    @Query(nativeQuery = true, value = "SELECT * FROM \"User\" WHERE \"Uuid\" = :uuid")
    Optional<UserEntity> findByUuid(@Param("uuid") UUID uuid);
}
