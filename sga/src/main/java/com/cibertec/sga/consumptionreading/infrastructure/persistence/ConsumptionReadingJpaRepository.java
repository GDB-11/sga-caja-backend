package com.cibertec.sga.consumptionreading.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ConsumptionReading}. Solo métodos {@code save} heredado
 * y consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ConsumptionReadingJpaRepository extends JpaRepository<ConsumptionReadingEntity, Long> {

    String ROW_SELECT = """
        SELECT cr."Uuid" AS uuid, ar."Uuid" AS account_receivable_uuid,
               cr."InitialReading" AS initial_reading, cr."FinalReading" AS final_reading,
               cr."UnitCost" AS unit_cost, cr."CalculatedAmount" AS calculated_amount
        FROM "ConsumptionReading" cr
        JOIN "AccountReceivable" ar ON ar."Id" = cr."AccountReceivableId"
        """;

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE cr.\"Uuid\" = :uuid")
    Optional<ConsumptionReadingRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE ar.\"Uuid\" = :accountReceivableUuid")
    Optional<ConsumptionReadingRow> findRowByAccountReceivableUuid(@Param("accountReceivableUuid") UUID accountReceivableUuid);

    @Query(nativeQuery = true, value = """
        SELECT EXISTS(
            SELECT 1 FROM "ConsumptionReading" cr
            JOIN "AccountReceivable" ar ON ar."Id" = cr."AccountReceivableId"
            WHERE ar."Uuid" = :accountReceivableUuid
        )
        """)
    boolean existsByAccountReceivableUuid(@Param("accountReceivableUuid") UUID accountReceivableUuid);
}
