package com.cibertec.sga.receipt.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Receipt}. Solo {@code save} heredado y una consulta
 * propia con {@code @Query(nativeQuery = true)} para resolver el {@code Id} interno desde el
 * {@code Uuid} (usada por los módulos que referencian un {@code Receipt} recién creado:
 * {@code payment}, {@code bankexchange}, {@code income}).
 */
public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"Receipt\" WHERE \"Uuid\" = :uuid")
    Optional<ReceiptEntity> findEntityByUuid(@Param("uuid") UUID uuid);
}
