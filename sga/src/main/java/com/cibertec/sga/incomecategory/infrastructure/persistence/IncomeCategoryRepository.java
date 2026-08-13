package com.cibertec.sga.incomecategory.infrastructure.persistence;

import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.incomecategory.domain.repository.IIncomeCategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class IncomeCategoryRepository implements IIncomeCategoryRepository {

    private final IncomeCategoryJpaRepository jpaRepository;
    private final IncomeCategoryMapper mapper;

    public IncomeCategoryRepository(IncomeCategoryJpaRepository jpaRepository, IncomeCategoryMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<IncomeCategory> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<IncomeCategory> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }
}
