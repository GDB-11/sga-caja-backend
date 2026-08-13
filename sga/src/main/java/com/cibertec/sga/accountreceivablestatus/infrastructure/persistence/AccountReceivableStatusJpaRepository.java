package com.cibertec.sga.accountreceivablestatus.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code AccountReceivableStatus}. Solo {@code findAll} heredado
 * y consultas propias con {@code @Query(nativeQuery = true)} — sin derived methods ni JPQL.
 */
public interface AccountReceivableStatusJpaRepository extends JpaRepository<AccountReceivableStatusEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"AccountReceivableStatus\" WHERE \"Uuid\" = :uuid")
    Optional<AccountReceivableStatusEntity> findByUuid(@Param("uuid") UUID uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM \"AccountReceivableStatus\" WHERE \"Name\" = :name")
    Optional<AccountReceivableStatusEntity> findByName(@Param("name") String name);
}
