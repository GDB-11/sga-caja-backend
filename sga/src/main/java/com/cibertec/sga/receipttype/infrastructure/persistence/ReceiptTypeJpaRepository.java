package com.cibertec.sga.receipttype.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ReceiptType}. Solo {@code findAll} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ReceiptTypeJpaRepository extends JpaRepository<ReceiptTypeEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"ReceiptType\" WHERE \"Uuid\" = :uuid")
    Optional<ReceiptTypeEntity> findByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"ReceiptType\" WHERE \"Name\" = :name")
    Optional<ReceiptTypeEntity> findByName(@Param("name") String name);
}
