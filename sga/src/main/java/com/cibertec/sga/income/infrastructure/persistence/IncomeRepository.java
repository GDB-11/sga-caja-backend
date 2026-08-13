package com.cibertec.sga.income.infrastructure.persistence;

import com.cibertec.sga.income.domain.model.Income;
import com.cibertec.sga.income.domain.repository.IIncomeRepository;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class IncomeRepository implements IIncomeRepository {

    private final IncomeJpaRepository jpaRepository;
    private final IncomeMapper mapper;

    public IncomeRepository(IncomeJpaRepository jpaRepository, IncomeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Income create(Receipt receipt, String depositorName, IncomeCategory incomeCategory, String concept, BigDecimal amount) {
        IncomeEntity saved = jpaRepository.save(mapper.toNewEntity(receipt, depositorName, incomeCategory, concept, amount));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public Optional<Income> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Page<Income> search(UUID incomeCategoryUuid, LocalDate date, Pageable pageable) {
        return jpaRepository.search(incomeCategoryUuid, date, pageable).map(mapper::toDomain);
    }
}
