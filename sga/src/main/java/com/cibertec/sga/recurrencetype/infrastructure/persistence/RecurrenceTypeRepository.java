package com.cibertec.sga.recurrencetype.infrastructure.persistence;

import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.recurrencetype.domain.repository.IRecurrenceTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RecurrenceTypeRepository implements IRecurrenceTypeRepository {

    private final RecurrenceTypeJpaRepository jpaRepository;
    private final RecurrenceTypeMapper mapper;

    public RecurrenceTypeRepository(RecurrenceTypeJpaRepository jpaRepository, RecurrenceTypeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RecurrenceType> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<RecurrenceType> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }
}
