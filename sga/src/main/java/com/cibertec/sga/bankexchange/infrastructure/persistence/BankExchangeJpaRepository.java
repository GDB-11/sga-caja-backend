package com.cibertec.sga.bankexchange.infrastructure.persistence;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code BankExchange}. Solo {@code save} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface BankExchangeJpaRepository extends JpaRepository<BankExchangeEntity, Long> {

    String ROW_SELECT = """
        SELECT be."Uuid" AS uuid, ar."Uuid" AS account_receivable_uuid, bk."Uuid" AS bank_uuid,
               r."Uuid" AS receipt_uuid, r."CorrelativeNumber" AS receipt_correlative_number,
               r."IssueDate" AS receipt_issue_date, r."Amount" AS receipt_amount, r."Description" AS receipt_description,
               rt."Uuid" AS receipt_type_uuid, rt."Name" AS receipt_type_name,
               be."DepositDate" AS deposit_date, be."Amount" AS amount
        FROM "BankExchange" be
        JOIN "AccountReceivable" ar ON ar."Id" = be."AccountReceivableId"
        JOIN "Bank" bk ON bk."Id" = be."BankId"
        JOIN "Receipt" r ON r."Id" = be."ReceiptId"
        JOIN "ReceiptType" rt ON rt."Id" = r."ReceiptTypeId"
        """;

    @Query(nativeQuery = true, value = ROW_SELECT + " WHERE be.\"Uuid\" = :uuid")
    Optional<BankExchangeRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = ROW_SELECT + """
        WHERE (CAST(:bankUuid AS uuid) IS NULL OR bk."Uuid" = CAST(:bankUuid AS uuid))
          AND (CAST(:date AS date) IS NULL OR be."DepositDate" = CAST(:date AS date))
        ORDER BY be."DepositDate" DESC, be."Id" DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM "BankExchange" be
        JOIN "Bank" bk ON bk."Id" = be."BankId"
        WHERE (CAST(:bankUuid AS uuid) IS NULL OR bk."Uuid" = CAST(:bankUuid AS uuid))
          AND (CAST(:date AS date) IS NULL OR be."DepositDate" = CAST(:date AS date))
        """)
    Page<BankExchangeRow> search(@Param("bankUuid") UUID bankUuid, @Param("date") LocalDate date, Pageable pageable);
}
