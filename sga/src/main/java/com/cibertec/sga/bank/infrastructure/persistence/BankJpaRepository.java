package com.cibertec.sga.bank.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Bank}. Solo métodos {@code save} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface BankJpaRepository extends JpaRepository<BankEntity, Long> {

    @Query(nativeQuery = true, value = """
        SELECT b."Uuid" AS uuid, b."Name" AS name, b."AccountNumber" AS account_number, b."Cci" AS cci,
               c."Uuid" AS currency_uuid, c."Code" AS currency_code, c."Name" AS currency_name,
               b."IsActive" AS is_active
        FROM "Bank" b JOIN "Currency" c ON c."Id" = b."CurrencyId"
        WHERE (CAST(:search AS varchar) IS NULL
               OR b."Name" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR b."AccountNumber" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, b."IsActive") = b."IsActive"
        ORDER BY b."Name"
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Bank" b
        WHERE (CAST(:search AS varchar) IS NULL
               OR b."Name" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR b."AccountNumber" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, b."IsActive") = b."IsActive"
        """)
    Page<BankRow> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT b."Uuid" AS uuid, b."Name" AS name, b."AccountNumber" AS account_number, b."Cci" AS cci,
               c."Uuid" AS currency_uuid, c."Code" AS currency_code, c."Name" AS currency_name,
               b."IsActive" AS is_active
        FROM "Bank" b JOIN "Currency" c ON c."Id" = b."CurrencyId"
        WHERE b."Uuid" = :uuid
        """)
    Optional<BankRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Bank\" WHERE \"Uuid\" = :uuid")
    Optional<BankEntity> findEntityByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM \"Bank\" WHERE \"AccountNumber\" = :accountNumber)")
    boolean existsByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT 1 FROM \"Bank\" WHERE \"AccountNumber\" = :accountNumber AND \"Uuid\" <> :uuid)")
    boolean existsByAccountNumberAndUuidNot(@Param("accountNumber") String accountNumber, @Param("uuid") UUID uuid);
}
