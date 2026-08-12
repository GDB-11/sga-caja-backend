package com.cibertec.sga.member.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Member}. Solo métodos {@code save} heredado y consultas
 * propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL. Filtros
 * opcionales de {@link #search} vía {@code COALESCE(:param, columna) = columna} (activo) y
 * {@code :param IS NULL OR columna ILIKE ...} (texto, ver nota del plan de implementación).
 */
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    @Query(nativeQuery = true, value = """
        SELECT m."Uuid" AS uuid, m."Code" AS code, m."FirstName" AS first_name, m."LastName" AS last_name,
               m."ShareNumber" AS share_number, s."Uuid" AS stage_uuid, s."Code" AS stage_code,
               s."Name" AS stage_name, m."BirthDate" AS birth_date, m."IsActive" AS is_active,
               m."CreatedAt" AS created_at, m."UpdatedAt" AS updated_at
        FROM "Member" m JOIN "Stage" s ON s."Id" = m."StageId"
        WHERE (CAST(:search AS varchar) IS NULL
               OR m."Code" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR m."FirstName" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR m."LastName" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, m."IsActive") = m."IsActive"
        ORDER BY m."LastName", m."FirstName"
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Member" m
        WHERE (CAST(:search AS varchar) IS NULL
               OR m."Code" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR m."FirstName" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR m."LastName" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, m."IsActive") = m."IsActive"
        """)
    Page<MemberRow> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT m."Uuid" AS uuid, m."Code" AS code, m."FirstName" AS first_name, m."LastName" AS last_name,
               m."ShareNumber" AS share_number, s."Uuid" AS stage_uuid, s."Code" AS stage_code,
               s."Name" AS stage_name, m."BirthDate" AS birth_date, m."IsActive" AS is_active,
               m."CreatedAt" AS created_at, m."UpdatedAt" AS updated_at
        FROM "Member" m JOIN "Stage" s ON s."Id" = m."StageId"
        WHERE m."Uuid" = :uuid
        """)
    Optional<MemberRow> findRowByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Member\" WHERE \"Uuid\" = :uuid")
    Optional<MemberEntity> findEntityByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM \"Member\" WHERE \"Code\" = :code)")
    boolean existsByCode(@Param("code") String code);

    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT 1 FROM \"Member\" WHERE \"Code\" = :code AND \"Uuid\" <> :uuid)")
    boolean existsByCodeAndUuidNot(@Param("code") String code, @Param("uuid") UUID uuid);
}
