package com.cibertec.sga.accountreceivablestatus.infrastructure.persistence;

import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.domain.repository.IAccountReceivableStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AccountReceivableStatusRepository implements IAccountReceivableStatusRepository {

    private final AccountReceivableStatusJpaRepository jpaRepository;
    private final AccountReceivableStatusMapper mapper;

    public AccountReceivableStatusRepository(AccountReceivableStatusJpaRepository jpaRepository, AccountReceivableStatusMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AccountReceivableStatus> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<AccountReceivableStatus> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<AccountReceivableStatus> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }
}
