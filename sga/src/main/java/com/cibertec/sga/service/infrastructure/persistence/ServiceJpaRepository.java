package com.cibertec.sga.service.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Service}. Solo métodos {@code save} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, Long> {

    @Query(nativeQuery = true, value = """
        SELECT sv."Uuid" AS uuid, sv."Name" AS name,
               rt."Uuid" AS recurrence_type_uuid, rt."Name" AS recurrence_type_name,
               ctt."Uuid" AS charge_target_type_uuid, ctt."Name" AS charge_target_type_name,
               c."Uuid" AS currency_uuid, c."Code" AS currency_code, c."Name" AS currency_name,
               sv."IsConsumptionBased" AS is_consumption_based, sv."Cost" AS cost, sv."UnitCost" AS unit_cost,
               sv."IsActive" AS is_active
        FROM "Service" sv
        JOIN "RecurrenceType" rt ON rt."Id" = sv."RecurrenceTypeId"
        JOIN "ChargeTargetType" ctt ON ctt."Id" = sv."ChargeTargetTypeId"
        JOIN "Currency" c ON c."Id" = sv."CurrencyId"
        WHERE (CAST(:search AS varchar) IS NULL OR sv."Name" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, sv."IsActive") = sv."IsActive"
        ORDER BY sv."Name"
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Service" sv
        WHERE (CAST(:search AS varchar) IS NULL OR sv."Name" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, sv."IsActive") = sv."IsActive"
        """)
    Page<ServiceRow> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT sv."Uuid" AS uuid, sv."Name" AS name,
               rt."Uuid" AS recurrence_type_uuid, rt."Name" AS recurrence_type_name,
               ctt."Uuid" AS charge_target_type_uuid, ctt."Name" AS charge_target_type_name,
               c."Uuid" AS currency_uuid, c."Code" AS currency_code, c."Name" AS currency_name,
               sv."IsConsumptionBased" AS is_consumption_based, sv."Cost" AS cost, sv."UnitCost" AS unit_cost,
               sv."IsActive" AS is_active
        FROM "Service" sv
        JOIN "RecurrenceType" rt ON rt."Id" = sv."RecurrenceTypeId"
        JOIN "ChargeTargetType" ctt ON ctt."Id" = sv."ChargeTargetTypeId"
        JOIN "Currency" c ON c."Id" = sv."CurrencyId"
        WHERE sv."Uuid" = :uuid
        """)
    Optional<ServiceRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Service\" WHERE \"Uuid\" = :uuid")
    Optional<ServiceEntity> findEntityByUuid(@Param("uuid") UUID uuid);
}
