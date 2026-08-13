package com.cibertec.sga.expense.infrastructure.persistence;

import com.cibertec.sga.expense.domain.model.ExpenseBulkUploadRef;
import com.cibertec.sga.expense.domain.repository.IExpenseBulkUploadRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseBulkUploadRepository implements IExpenseBulkUploadRepository {

    private final ExpenseBulkUploadJpaRepository jpaRepository;
    private final ExpenseBulkUploadStatusJpaRepository statusJpaRepository;

    public ExpenseBulkUploadRepository(
        ExpenseBulkUploadJpaRepository jpaRepository, ExpenseBulkUploadStatusJpaRepository statusJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.statusJpaRepository = statusJpaRepository;
    }

    @Override
    public ExpenseBulkUploadRef create(String fileName, String statusName) {
        Long statusId = statusJpaRepository.findByName(statusName).orElseThrow().getId();
        ExpenseBulkUploadEntity saved = jpaRepository.save(
            ExpenseBulkUploadEntity.builder().fileName(fileName).expenseBulkUploadStatusId(statusId).build()
        );
        return new ExpenseBulkUploadRef(saved.getUuid(), saved.getFileName());
    }
}
