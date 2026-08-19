package com.cibertec.sga.receipt.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Receipt}. Solo {@code save} heredado y consultas propias
 * con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"Receipt\" WHERE \"Uuid\" = :uuid")
    Optional<ReceiptEntity> findEntityByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = """
        SELECT r."Uuid" AS uuid, rt."Uuid" AS receipt_type_uuid, rt."Name" AS receipt_type_name,
               r."CorrelativeNumber" AS correlative_number, r."IssueDate" AS issue_date,
               r."Amount" AS amount, r."Description" AS description,
               cur."Uuid" AS currency_uuid, cur."Code" AS currency_code, cur."Name" AS currency_name
        FROM "Receipt" r
        JOIN "ReceiptType" rt ON rt."Id" = r."ReceiptTypeId"
        JOIN "Currency" cur ON cur."Id" = r."CurrencyId"
        WHERE r."IssueDate" BETWEEN :startDate AND :endDate
        ORDER BY r."IssueDate", rt."Name", r."CorrelativeNumber"
        """)
    List<ReceiptRow> findRowsByIssueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
