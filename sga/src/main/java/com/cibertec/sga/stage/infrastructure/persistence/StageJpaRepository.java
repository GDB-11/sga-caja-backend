package com.cibertec.sga.stage.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Stage}. Solo {@code findAll} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface StageJpaRepository extends JpaRepository<StageEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"Stage\" WHERE \"Uuid\" = :uuid")
    Optional<StageEntity> findByUuid(@Param("uuid") UUID uuid);
}
