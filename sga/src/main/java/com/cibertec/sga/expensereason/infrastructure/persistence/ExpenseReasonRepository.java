package com.cibertec.sga.expensereason.infrastructure.persistence;

import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensereason.domain.repository.IExpenseReasonRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseReasonRepository implements IExpenseReasonRepository {

    private final ExpenseReasonJpaRepository jpaRepository;
    private final ExpenseReasonMapper mapper;

    public ExpenseReasonRepository(ExpenseReasonJpaRepository jpaRepository, ExpenseReasonMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ExpenseReason> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ExpenseReason> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<ExpenseReason> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }
}
