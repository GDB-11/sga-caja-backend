package com.cibertec.sga.recurrencetype.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code RecurrenceType}. Solo {@code findAll} heredado y
 * consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface RecurrenceTypeJpaRepository extends JpaRepository<RecurrenceTypeEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"RecurrenceType\" WHERE \"Uuid\" = :uuid")
    Optional<RecurrenceTypeEntity> findByUuid(@Param("uuid") UUID uuid);
}
