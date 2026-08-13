package com.cibertec.sga.expensestatus.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ExpenseStatus}. Solo {@code findAll} heredado y
 * consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ExpenseStatusJpaRepository extends JpaRepository<ExpenseStatusEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"ExpenseStatus\" WHERE \"Uuid\" = :uuid")
    Optional<ExpenseStatusEntity> findByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"ExpenseStatus\" WHERE \"Name\" = :name")
    Optional<ExpenseStatusEntity> findByName(@Param("name") String name);
}
