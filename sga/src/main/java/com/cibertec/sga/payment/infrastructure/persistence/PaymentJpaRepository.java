package com.cibertec.sga.payment.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Payment}. Solo {@code save} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    String ROW_SELECT = """
        SELECT p."Uuid" AS uuid, p."PaymentDate" AS payment_date, p."TotalAmount" AS total_amount,
               r."Uuid" AS receipt_uuid, r."CorrelativeNumber" AS receipt_correlative_number,
               r."IssueDate" AS receipt_issue_date, r."Amount" AS receipt_amount, r."Description" AS receipt_description,
               rt."Uuid" AS receipt_type_uuid, rt."Name" AS receipt_type_name
        FROM "Payment" p
        JOIN "Receipt" r ON r."Id" = p."ReceiptId"
        JOIN "ReceiptType" rt ON rt."Id" = r."ReceiptTypeId"
        """;

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE p.\"Uuid\" = :uuid")
    Optional<PaymentRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Payment\" WHERE \"Uuid\" = :uuid")
    Optional<PaymentEntity> findEntityByUuid(@Param("uuid") UUID uuid);
}
