package com.cibertec.sga.businesstype.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code BusinessType}. Solo métodos {@code save}/{@code delete}
 * heredados y consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface BusinessTypeJpaRepository extends JpaRepository<BusinessTypeEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"BusinessType\" WHERE \"Uuid\" = :uuid")
    Optional<BusinessTypeEntity> findByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM \"BusinessType\" WHERE \"Name\" = :name)")
    boolean existsByName(@Param("name") String name);

    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT 1 FROM \"BusinessType\" WHERE \"Name\" = :name AND \"Uuid\" <> :uuid)")
    boolean existsByNameAndUuidNot(@Param("name") String name, @Param("uuid") UUID uuid);
}
