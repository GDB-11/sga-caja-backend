package com.cibertec.sga.consumptionreading.infrastructure.persistence;

import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import com.cibertec.sga.consumptionreading.domain.repository.IConsumptionReadingRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ConsumptionReadingRepository implements IConsumptionReadingRepository {

    private final ConsumptionReadingJpaRepository jpaRepository;
    private final ConsumptionReadingMapper mapper;

    public ConsumptionReadingRepository(ConsumptionReadingJpaRepository jpaRepository, ConsumptionReadingMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ConsumptionReading> findByUuid(UUID uuid) {
        return jpaRepository.findRowByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<ConsumptionReading> findByAccountReceivableUuid(UUID accountReceivableUuid) {
        return jpaRepository.findRowByAccountReceivableUuid(accountReceivableUuid).map(mapper::toDomain);
    }

    @Override
    public boolean existsByAccountReceivableUuid(UUID accountReceivableUuid) {
        return jpaRepository.existsByAccountReceivableUuid(accountReceivableUuid);
    }

    @Override
    public ConsumptionReading insert(ConsumptionReading consumptionReading) {
        ConsumptionReadingEntity saved = jpaRepository.save(mapper.toNewEntity(consumptionReading));
        return findByUuid(saved.getUuid()).orElseThrow();
    }
}
