package com.cibertec.sga.currency.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Currency}. Solo {@code findAll} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface CurrencyJpaRepository extends JpaRepository<CurrencyEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"Currency\" WHERE \"Uuid\" = :uuid")
    Optional<CurrencyEntity> findByUuid(@Param("uuid") UUID uuid);
}
