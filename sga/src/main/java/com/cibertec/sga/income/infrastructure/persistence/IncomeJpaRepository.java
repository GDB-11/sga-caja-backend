package com.cibertec.sga.income.infrastructure.persistence;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Income}. Solo {@code save} heredado y consultas propias
 * con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface IncomeJpaRepository extends JpaRepository<IncomeEntity, Long> {

    String ROW_SELECT = """
        SELECT i."Uuid" AS uuid, r."Uuid" AS receipt_uuid, r."CorrelativeNumber" AS receipt_correlative_number,
               r."IssueDate" AS receipt_issue_date, rt."Uuid" AS receipt_type_uuid, rt."Name" AS receipt_type_name,
               i."DepositorName" AS depositor_name, ic."Uuid" AS income_category_uuid, ic."Name" AS income_category_name,
               i."Concept" AS concept, i."Amount" AS amount
        FROM "Income" i
        JOIN "Receipt" r ON r."Id" = i."ReceiptId"
        JOIN "ReceiptType" rt ON rt."Id" = r."ReceiptTypeId"
        JOIN "IncomeCategory" ic ON ic."Id" = i."IncomeCategoryId"
        """;

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE i.\"Uuid\" = :uuid")
    Optional<IncomeRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = ROW_SELECT + """
        WHERE (CAST(:incomeCategoryUuid AS uuid) IS NULL OR ic."Uuid" = CAST(:incomeCategoryUuid AS uuid))
          AND (CAST(:date AS date) IS NULL OR r."IssueDate" = CAST(:date AS date))
        ORDER BY r."IssueDate" DESC, i."Id" DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Income" i
        JOIN "Receipt" r ON r."Id" = i."ReceiptId"
        JOIN "IncomeCategory" ic ON ic."Id" = i."IncomeCategoryId"
        WHERE (CAST(:incomeCategoryUuid AS uuid) IS NULL OR ic."Uuid" = CAST(:incomeCategoryUuid AS uuid))
          AND (CAST(:date AS date) IS NULL OR r."IssueDate" = CAST(:date AS date))
        """)
    Page<IncomeRow> search(
        @Param("incomeCategoryUuid") UUID incomeCategoryUuid, @Param("date") LocalDate date, Pageable pageable
    );
}
