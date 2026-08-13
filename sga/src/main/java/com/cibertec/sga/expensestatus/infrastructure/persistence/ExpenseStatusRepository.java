package com.cibertec.sga.expensestatus.infrastructure.persistence;

import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.expensestatus.domain.repository.IExpenseStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseStatusRepository implements IExpenseStatusRepository {

    private final ExpenseStatusJpaRepository jpaRepository;
    private final ExpenseStatusMapper mapper;

    public ExpenseStatusRepository(ExpenseStatusJpaRepository jpaRepository, ExpenseStatusMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ExpenseStatus> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ExpenseStatus> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<ExpenseStatus> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }
}
