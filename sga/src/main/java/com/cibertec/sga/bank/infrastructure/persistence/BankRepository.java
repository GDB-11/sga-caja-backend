package com.cibertec.sga.bank.infrastructure.persistence;

import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bank.domain.repository.IBankRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BankRepository implements IBankRepository {

    private final BankJpaRepository jpaRepository;
    private final BankMapper mapper;

    public BankRepository(BankJpaRepository jpaRepository, BankMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Bank> search(String search, Boolean active, Pageable pageable) {
        return jpaRepository.search(search, active, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Bank> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public boolean existsByAccountNumberAndUuidNot(String accountNumber, UUID uuid) {
        return jpaRepository.existsByAccountNumberAndUuidNot(accountNumber, uuid);
    }

    @Override
    public Bank insert(Bank bank) {
        BankEntity saved = jpaRepository.save(mapper.toNewEntity(bank));
        return findByUuid(saved.getUuid()).orElseThrow();
    }

    @Override
    public Bank update(UUID uuid, Bank bank) {
        BankEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        mapper.updateEntity(entity, bank);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public Bank deactivate(UUID uuid) {
        BankEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setActive(false);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }
}
