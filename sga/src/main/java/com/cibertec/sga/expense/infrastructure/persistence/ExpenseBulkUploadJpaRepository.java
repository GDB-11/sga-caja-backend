package com.cibertec.sga.expense.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ExpenseBulkUpload}. {@code save} heredado para crearlo
 * con su estado final ya resuelto (RF-28), más una consulta propia para resolver su {@code Id}
 * interno desde el {@code Uuid} (usada por {@code ExpenseMapper} al vincular los egresos del
 * lote).
 */
public interface ExpenseBulkUploadJpaRepository extends JpaRepository<ExpenseBulkUploadEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"ExpenseBulkUpload\" WHERE \"Uuid\" = :uuid")
    Optional<ExpenseBulkUploadEntity> findEntityByUuid(@Param("uuid") UUID uuid);
}
