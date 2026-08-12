package com.cibertec.sga.role.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"Role\" WHERE \"Uuid\" = :uuid")
    Optional<RoleEntity> findByUuid(@Param("uuid") UUID uuid);
}
