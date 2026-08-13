package com.cibertec.sga.accountreceivable.infrastructure.persistence;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class AccountReceivableRepository implements IAccountReceivableRepository {

    private final AccountReceivableJpaRepository jpaRepository;
    private final AccountReceivableMapper mapper;

    public AccountReceivableRepository(AccountReceivableJpaRepository jpaRepository, AccountReceivableMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<AccountReceivable> search(UUID serviceUuid, UUID memberUuid, UUID stallUuid, Pageable pageable) {
        return jpaRepository.search(serviceUuid, memberUuid, stallUuid, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<AccountReceivable> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<AccountReceivable> findByUuidForUpdate(UUID uuid) {
        if (jpaRepository.lockEntityByUuid(uuid).isEmpty()) {
            return Optional.empty();
        }
        return findByUuid(uuid);
    }

    @Override
    public List<AccountReceivable> insertAll(List<AccountReceivable> accountReceivables) {
        if (accountReceivables.isEmpty()) {
            return List.of();
        }
        List<AccountReceivableEntity> entities = accountReceivables.stream().map(mapper::toNewEntity).toList();
        List<AccountReceivableEntity> saved = jpaRepository.saveAll(entities);
        List<UUID> uuids = saved.stream().map(AccountReceivableEntity::getUuid).toList();
        return jpaRepository.findRowsByUuids(uuids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public AccountReceivable updateAmount(UUID uuid, BigDecimal amount) {
        AccountReceivableEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setAmount(amount);
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public AccountReceivable updateStatus(UUID uuid, AccountReceivableStatus status) {
        AccountReceivableEntity entity = jpaRepository.findEntityByUuid(uuid).orElseThrow();
        entity.setAccountReceivableStatusId(mapper.resolveStatusId(status));
        jpaRepository.save(entity);
        return findByUuid(uuid).orElseThrow();
    }

    @Override
    public List<AccountReceivableMovement> findMovementsByMember(UUID memberUuid) {
        return jpaRepository.findSummaryRowsByMember(memberUuid).stream().map(mapper::toMovement).toList();
    }

    @Override
    public List<AccountReceivableMovement> findMovementsByStall(UUID stallUuid) {
        return jpaRepository.findSummaryRowsByStall(stallUuid).stream().map(mapper::toMovement).toList();
    }

    @Override
    public List<AccountReceivableMovement> findMovementsByMemberPeriod(int year, int month) {
        return jpaRepository.findSummaryRowsByMemberPeriod(year, month).stream().map(mapper::toMovement).toList();
    }

    @Override
    public List<AccountReceivableMovement> findMovementsByStallPeriod(int year, int month) {
        return jpaRepository.findSummaryRowsByStallPeriod(year, month).stream().map(mapper::toMovement).toList();
    }
}
