package com.cibertec.sga.expense.infrastructure.persistence;

import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expense.domain.repository.IExpenseRepository;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseRepository implements IExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;
    private final ExpenseMapper mapper;

    public ExpenseRepository(ExpenseJpaRepository jpaRepository, ExpenseMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Expense insert(Expense expense) {
        ExpenseEntity saved = jpaRepository.save(mapper.toNewEntity(expense));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public List<Expense> insertAll(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            return List.of();
        }
        List<ExpenseEntity> entities = expenses.stream().map(mapper::toNewEntity).toList();
        List<ExpenseEntity> saved = jpaRepository.saveAll(entities);
        List<UUID> uuids = saved.stream().map(ExpenseEntity::getUuid).toList();
        return jpaRepository.findRowsByUuids(uuids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Expense> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Page<Expense> search(Integer year, Integer month, Pageable pageable) {
        return jpaRepository.search(year, month, pageable).map(mapper::toDomain);
    }

    @Override
    public Expense updateStatus(UUID uuid, ExpenseStatus status) {
        ExpenseEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setExpenseStatusId(mapper.resolveStatusId(status));
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public Expense markProcessed(UUID uuid, ExpenseStatus processedStatus, Receipt receipt) {
        ExpenseEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setExpenseStatusId(mapper.resolveStatusId(processedStatus));
        entity.setReceiptId(mapper.resolveReceiptId(receipt));
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }
}
