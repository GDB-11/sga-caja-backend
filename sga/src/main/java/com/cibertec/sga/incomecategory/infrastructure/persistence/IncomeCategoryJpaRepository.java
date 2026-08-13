package com.cibertec.sga.incomecategory.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code IncomeCategory}. Solo {@code findAll} heredado y
 * consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface IncomeCategoryJpaRepository extends JpaRepository<IncomeCategoryEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"IncomeCategory\" WHERE \"Uuid\" = :uuid")
    Optional<IncomeCategoryEntity> findByUuid(@Param("uuid") UUID uuid);
}
