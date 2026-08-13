package com.cibertec.sga.accountreceivable.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code AccountReceivable}. Solo métodos {@code save}/{@code
 * saveAll} heredados y consultas propias con {@code @Query(nativeQuery = true)} — sin derived
 * methods ni JPQL.
 */
public interface AccountReceivableJpaRepository extends JpaRepository<AccountReceivableEntity, Long> {

    String ROW_SELECT = """
        SELECT ar."Uuid" AS uuid,
               sv."Uuid" AS service_uuid, sv."Name" AS service_name,
               rt."Uuid" AS recurrence_type_uuid, rt."Name" AS recurrence_type_name,
               ctt."Uuid" AS charge_target_type_uuid, ctt."Name" AS charge_target_type_name,
               cur."Uuid" AS currency_uuid, cur."Code" AS currency_code, cur."Name" AS currency_name,
               sv."IsConsumptionBased" AS service_is_consumption_based, sv."Cost" AS service_cost,
               sv."UnitCost" AS service_unit_cost, sv."IsActive" AS service_is_active,
               mem."Uuid" AS member_uuid, CONCAT(mem."FirstName", ' ', mem."LastName") AS member_full_name,
               st."Uuid" AS stall_uuid, st."Number" AS stall_number,
               ar."PeriodStartDate" AS period_start_date, ar."PeriodEndDate" AS period_end_date,
               ar."Amount" AS amount, ars."Uuid" AS status_uuid, ars."Name" AS status_name
        FROM "AccountReceivable" ar
        JOIN "Service" sv ON sv."Id" = ar."ServiceId"
        JOIN "RecurrenceType" rt ON rt."Id" = sv."RecurrenceTypeId"
        JOIN "ChargeTargetType" ctt ON ctt."Id" = sv."ChargeTargetTypeId"
        JOIN "Currency" cur ON cur."Id" = sv."CurrencyId"
        LEFT JOIN "Member" mem ON mem."Id" = ar."MemberId"
        LEFT JOIN "Stall" st ON st."Id" = ar."StallId"
        JOIN "AccountReceivableStatus" ars ON ars."Id" = ar."AccountReceivableStatusId"
        """;

    @Query(nativeQuery = true, value = ROW_SELECT + """
        WHERE (CAST(:serviceUuid AS uuid) IS NULL OR sv."Uuid" = CAST(:serviceUuid AS uuid))
          AND (CAST(:memberUuid AS uuid) IS NULL OR mem."Uuid" = CAST(:memberUuid AS uuid))
          AND (CAST(:stallUuid AS uuid) IS NULL OR st."Uuid" = CAST(:stallUuid AS uuid))
        ORDER BY ar."PeriodStartDate" DESC, ar."Id" DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM "AccountReceivable" ar
        JOIN "Service" sv ON sv."Id" = ar."ServiceId"
        LEFT JOIN "Member" mem ON mem."Id" = ar."MemberId"
        LEFT JOIN "Stall" st ON st."Id" = ar."StallId"
        WHERE (CAST(:serviceUuid AS uuid) IS NULL OR sv."Uuid" = CAST(:serviceUuid AS uuid))
          AND (CAST(:memberUuid AS uuid) IS NULL OR mem."Uuid" = CAST(:memberUuid AS uuid))
          AND (CAST(:stallUuid AS uuid) IS NULL OR st."Uuid" = CAST(:stallUuid AS uuid))
        """)
    Page<AccountReceivableRow> search(
        @Param("serviceUuid") UUID serviceUuid, @Param("memberUuid") UUID memberUuid, @Param("stallUuid") UUID stallUuid,
        Pageable pageable
    );

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE ar.\"Uuid\" = :uuid")
    Optional<AccountReceivableRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE ar.\"Uuid\" IN (:uuids)")
    List<AccountReceivableRow> findRowsByUuids(@Param("uuids") List<UUID> uuids);

    @Query(nativeQuery = true, value = "SELECT * FROM \"AccountReceivable\" WHERE \"Uuid\" = :uuid")
    Optional<AccountReceivableEntity> findEntityByUuid(@Param("uuid") UUID uuid);

    /**
     * Toma un bloqueo pesimista de fila sobre la cuenta por cobrar (RNF-04) — el valor devuelto
     * no se usa, solo sirve para bloquear hasta que cualquier transacción concurrente que la
     * tenga bloqueada confirme o revierta; el estado real se relee después vía
     * {@link #findRowByUuid(UUID)}, que ve el dato ya actualizado por esa transacción.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM \"AccountReceivable\" WHERE \"Uuid\" = :uuid FOR UPDATE")
    Optional<AccountReceivableEntity> lockEntityByUuid(@Param("uuid") UUID uuid);

    String SUMMARY_ROW_SELECT = """
        SELECT ar."Uuid" AS uuid,
               sv."Uuid" AS service_uuid, sv."Name" AS service_name,
               rt."Uuid" AS recurrence_type_uuid, rt."Name" AS recurrence_type_name,
               ctt."Uuid" AS charge_target_type_uuid, ctt."Name" AS charge_target_type_name,
               cur."Uuid" AS currency_uuid, cur."Code" AS currency_code, cur."Name" AS currency_name,
               sv."IsConsumptionBased" AS service_is_consumption_based, sv."Cost" AS service_cost,
               sv."UnitCost" AS service_unit_cost, sv."IsActive" AS service_is_active,
               mem."Uuid" AS member_uuid, CONCAT(mem."FirstName", ' ', mem."LastName") AS member_full_name,
               st."Uuid" AS stall_uuid, st."Number" AS stall_number,
               ar."PeriodStartDate" AS period_start_date, ar."PeriodEndDate" AS period_end_date,
               ar."Amount" AS amount, ars."Uuid" AS status_uuid, ars."Name" AS status_name,
               CASE WHEN pd."Id" IS NOT NULL THEN 'Payment' WHEN bx."Id" IS NOT NULL THEN 'BankExchange' END AS settlement_method,
               COALESCE(p."PaymentDate", bx."DepositDate") AS settled_date,
               COALESCE(pr."CorrelativeNumber", bxr."CorrelativeNumber") AS receipt_correlative
        FROM "AccountReceivable" ar
        JOIN "Service" sv ON sv."Id" = ar."ServiceId"
        JOIN "RecurrenceType" rt ON rt."Id" = sv."RecurrenceTypeId"
        JOIN "ChargeTargetType" ctt ON ctt."Id" = sv."ChargeTargetTypeId"
        JOIN "Currency" cur ON cur."Id" = sv."CurrencyId"
        LEFT JOIN "Member" mem ON mem."Id" = ar."MemberId"
        LEFT JOIN "Stall" st ON st."Id" = ar."StallId"
        JOIN "AccountReceivableStatus" ars ON ars."Id" = ar."AccountReceivableStatusId"
        LEFT JOIN "PaymentDetail" pd ON pd."AccountReceivableId" = ar."Id"
        LEFT JOIN "Payment" p ON p."Id" = pd."PaymentId"
        LEFT JOIN "Receipt" pr ON pr."Id" = p."ReceiptId"
        LEFT JOIN "BankExchange" bx ON bx."AccountReceivableId" = ar."Id"
        LEFT JOIN "Receipt" bxr ON bxr."Id" = bx."ReceiptId"
        """;

    @Query(nativeQuery = true, value = SUMMARY_ROW_SELECT + """
        WHERE mem."Uuid" = :memberUuid
        ORDER BY ar."PeriodStartDate" DESC, ar."Id" DESC
        """)
    List<AccountReceivableSummaryRow> findSummaryRowsByMember(@Param("memberUuid") UUID memberUuid);

    @Query(nativeQuery = true, value = SUMMARY_ROW_SELECT + """
        WHERE st."Uuid" = :stallUuid
        ORDER BY ar."PeriodStartDate" DESC, ar."Id" DESC
        """)
    List<AccountReceivableSummaryRow> findSummaryRowsByStall(@Param("stallUuid") UUID stallUuid);

    @Query(nativeQuery = true, value = SUMMARY_ROW_SELECT + """
        WHERE mem."Id" IS NOT NULL
          AND EXTRACT(YEAR FROM ar."PeriodStartDate") = :year
          AND EXTRACT(MONTH FROM ar."PeriodStartDate") = :month
        ORDER BY mem."LastName", mem."FirstName", ar."PeriodStartDate"
        """)
    List<AccountReceivableSummaryRow> findSummaryRowsByMemberPeriod(@Param("year") int year, @Param("month") int month);

    @Query(nativeQuery = true, value = SUMMARY_ROW_SELECT + """
        WHERE st."Id" IS NOT NULL
          AND EXTRACT(YEAR FROM ar."PeriodStartDate") = :year
          AND EXTRACT(MONTH FROM ar."PeriodStartDate") = :month
        ORDER BY st."Number", ar."PeriodStartDate"
        """)
    List<AccountReceivableSummaryRow> findSummaryRowsByStallPeriod(@Param("year") int year, @Param("month") int month);
}
