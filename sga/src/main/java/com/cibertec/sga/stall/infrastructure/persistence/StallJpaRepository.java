package com.cibertec.sga.stall.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Stall}. Solo métodos {@code save} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface StallJpaRepository extends JpaRepository<StallEntity, Long> {

    @Query(nativeQuery = true, value = """
        SELECT st."Uuid" AS uuid, st."Number" AS number,
               bt."Uuid" AS business_type_uuid, bt."Name" AS business_type_name,
               mem."Uuid" AS member_uuid, CONCAT(mem."FirstName", ' ', mem."LastName") AS member_full_name,
               st."TenantName" AS tenant_name, st."TenantDocument" AS tenant_document,
               st."ValidityStartDate" AS validity_start_date, st."ValidityEndDate" AS validity_end_date,
               st."IsActive" AS is_active
        FROM "Stall" st
        JOIN "BusinessType" bt ON bt."Id" = st."BusinessTypeId"
        LEFT JOIN "Member" mem ON mem."Id" = st."MemberId"
        WHERE (CAST(:search AS varchar) IS NULL
               OR st."Number" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR st."TenantName" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR st."TenantDocument" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, st."IsActive") = st."IsActive"
        ORDER BY st."Number"
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Stall" st
        WHERE (CAST(:search AS varchar) IS NULL
               OR st."Number" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR st."TenantName" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR st."TenantDocument" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, st."IsActive") = st."IsActive"
        """)
    Page<StallRow> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT st."Uuid" AS uuid, st."Number" AS number,
               bt."Uuid" AS business_type_uuid, bt."Name" AS business_type_name,
               mem."Uuid" AS member_uuid, CONCAT(mem."FirstName", ' ', mem."LastName") AS member_full_name,
               st."TenantName" AS tenant_name, st."TenantDocument" AS tenant_document,
               st."ValidityStartDate" AS validity_start_date, st."ValidityEndDate" AS validity_end_date,
               st."IsActive" AS is_active
        FROM "Stall" st
        JOIN "BusinessType" bt ON bt."Id" = st."BusinessTypeId"
        LEFT JOIN "Member" mem ON mem."Id" = st."MemberId"
        WHERE st."Uuid" = :uuid
        """)
    Optional<StallRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Stall\" WHERE \"Uuid\" = :uuid")
    Optional<StallEntity> findEntityByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = """
        SELECT st."Uuid" AS uuid, st."Number" AS number,
               bt."Uuid" AS business_type_uuid, bt."Name" AS business_type_name,
               mem."Uuid" AS member_uuid, CONCAT(mem."FirstName", ' ', mem."LastName") AS member_full_name,
               st."TenantName" AS tenant_name, st."TenantDocument" AS tenant_document,
               st."ValidityStartDate" AS validity_start_date, st."ValidityEndDate" AS validity_end_date,
               st."IsActive" AS is_active
        FROM "Stall" st
        JOIN "BusinessType" bt ON bt."Id" = st."BusinessTypeId"
        LEFT JOIN "Member" mem ON mem."Id" = st."MemberId"
        WHERE st."IsActive" = TRUE
        ORDER BY st."Number"
        """)
    List<StallRow> findAllActiveRows();

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM \"Stall\" WHERE \"Number\" = :number)")
    boolean existsByNumber(@Param("number") String number);

    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT 1 FROM \"Stall\" WHERE \"Number\" = :number AND \"Uuid\" <> :uuid)")
    boolean existsByNumberAndUuidNot(@Param("number") String number, @Param("uuid") UUID uuid);
}
