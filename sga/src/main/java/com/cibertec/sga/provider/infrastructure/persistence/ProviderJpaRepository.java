package com.cibertec.sga.provider.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code Provider}. Solo métodos {@code save} heredado y
 * consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface ProviderJpaRepository extends JpaRepository<ProviderEntity, Long> {

    @Query(nativeQuery = true, value = """
        SELECT * FROM "Provider" p
        WHERE (CAST(:search AS varchar) IS NULL
               OR p."Name" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR p."Document" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, p."IsActive") = p."IsActive"
        ORDER BY p."Name"
        """,
        countQuery = """
        SELECT COUNT(*) FROM "Provider" p
        WHERE (CAST(:search AS varchar) IS NULL
               OR p."Name" ILIKE CONCAT('%', CAST(:search AS varchar), '%')
               OR p."Document" ILIKE CONCAT('%', CAST(:search AS varchar), '%'))
          AND COALESCE(:active, p."IsActive") = p."IsActive"
        """)
    Page<ProviderEntity> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM \"Provider\" WHERE \"Uuid\" = :uuid")
    Optional<ProviderEntity> findByUuid(@Param("uuid") UUID uuid);
}
