package com.cibertec.sga.expense.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository de {@code ExpenseBulkUploadStatus} — interno al módulo {@code
 * expense}, ver {@link ExpenseBulkUploadStatusEntity}.
 */
public interface ExpenseBulkUploadStatusJpaRepository extends JpaRepository<ExpenseBulkUploadStatusEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM \"ExpenseBulkUploadStatus\" WHERE \"Name\" = :name")
    Optional<ExpenseBulkUploadStatusEntity> findByName(@Param("name") String name);
}
