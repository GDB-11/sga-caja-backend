package com.cibertec.sga.chargetargettype.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ChargeTargetType}. Solo {@code findAll} heredado y
 * consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ChargeTargetTypeJpaRepository extends JpaRepository<ChargeTargetTypeEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"ChargeTargetType\" WHERE \"Uuid\" = :uuid")
    Optional<ChargeTargetTypeEntity> findByUuid(@Param("uuid") UUID uuid);
}
