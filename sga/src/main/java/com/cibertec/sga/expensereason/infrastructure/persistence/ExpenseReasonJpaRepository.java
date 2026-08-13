package com.cibertec.sga.expensereason.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ExpenseReason}. Solo {@code findAll} heredado y
 * consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ExpenseReasonJpaRepository extends JpaRepository<ExpenseReasonEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"ExpenseReason\" WHERE \"Uuid\" = :uuid")
    Optional<ExpenseReasonEntity> findByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"ExpenseReason\" WHERE \"Name\" = :name")
    Optional<ExpenseReasonEntity> findByName(@Param("name") String name);
}
