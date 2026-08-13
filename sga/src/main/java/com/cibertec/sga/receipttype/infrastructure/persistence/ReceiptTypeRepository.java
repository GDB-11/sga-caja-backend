package com.cibertec.sga.receipttype.infrastructure.persistence;

import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.domain.repository.IReceiptTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiptTypeRepository implements IReceiptTypeRepository {

    private final ReceiptTypeJpaRepository jpaRepository;
    private final ReceiptTypeMapper mapper;

    public ReceiptTypeRepository(ReceiptTypeJpaRepository jpaRepository, ReceiptTypeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ReceiptType> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ReceiptType> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<ReceiptType> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }
}
