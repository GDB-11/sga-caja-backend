package com.cibertec.sga.expense.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Expense}. Solo métodos {@code save}/{@code saveAll}
 * heredados y consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni
 * JPQL.
 */
public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long> {

    String ROW_SELECT = """
        SELECT e."Uuid" AS uuid, e."DocumentNumber" AS document_number,
               p."Uuid" AS provider_uuid, p."Name" AS provider_name, p."Document" AS provider_document,
               p."IsActive" AS provider_is_active,
               e."ExpenseDate" AS expense_date, e."Amount" AS amount, e."AssociatedDocument" AS associated_document,
               er."Uuid" AS expense_reason_uuid, er."Name" AS expense_reason_name,
               es."Uuid" AS status_uuid, es."Name" AS status_name,
               r."Uuid" AS receipt_uuid, r."CorrelativeNumber" AS receipt_correlative_number,
               r."IssueDate" AS receipt_issue_date, rt."Uuid" AS receipt_type_uuid, rt."Name" AS receipt_type_name,
               ebu."Uuid" AS bulk_upload_uuid, ebu."FileName" AS bulk_upload_file_name
        FROM "Expense" e
        JOIN "Provider" p ON p."Id" = e."ProviderId"
        JOIN "ExpenseReason" er ON er."Id" = e."ExpenseReasonId"
        JOIN "ExpenseStatus" es ON es."Id" = e."ExpenseStatusId"
        LEFT JOIN "Receipt" r ON r."Id" = e."ReceiptId"
        LEFT JOIN "ReceiptType" rt ON rt."Id" = r."ReceiptTypeId"
        LEFT JOIN "ExpenseBulkUpload" ebu ON ebu."Id" = e."ExpenseBulkUploadId"
        """;

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE e.\"Uuid\" = :uuid")
    Optional<ExpenseRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE e.\"Uuid\" IN (:uuids)")
    List<ExpenseRow> findRowsByUuids(@Param("uuids") List<UUID> uuids);

    @Query(nativeQuery = true, value = ROW_SELECT + """
        WHERE (CAST(:year AS int) IS NULL OR EXTRACT(YEAR FROM e."ExpenseDate") = :year)
          AND (CAST(:month AS int) IS NULL OR EXTRACT(MONTH FROM e."ExpenseDate") = :month)
        ORDER BY e."ExpenseDate" DESC, e."Id" DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Expense" e
        WHERE (CAST(:year AS int) IS NULL OR EXTRACT(YEAR FROM e."ExpenseDate") = :year)
          AND (CAST(:month AS int) IS NULL OR EXTRACT(MONTH FROM e."ExpenseDate") = :month)
        """)
    Page<ExpenseRow> search(@Param("year") Integer year, @Param("month") Integer month, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Expense\" WHERE \"Uuid\" = :uuid")
    Optional<ExpenseEntity> findEntityByUuid(@Param("uuid") UUID uuid);
}
