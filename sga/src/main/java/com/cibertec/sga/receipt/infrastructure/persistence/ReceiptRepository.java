package com.cibertec.sga.receipt.infrastructure.persistence;

import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.domain.repository.IReceiptRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiptRepository implements IReceiptRepository {

    private final ReceiptJpaRepository jpaRepository;
    private final ReceiptMapper mapper;

    public ReceiptRepository(ReceiptJpaRepository jpaRepository, ReceiptMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Receipt insert(Receipt receipt) {
        ReceiptEntity saved = jpaRepository.save(mapper.toNewEntity(receipt));
        return mapper.toDomain(saved, receipt.getReceiptType());
    }
}
