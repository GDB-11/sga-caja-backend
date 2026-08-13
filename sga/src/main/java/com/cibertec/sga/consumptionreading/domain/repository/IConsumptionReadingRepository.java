package com.cibertec.sga.consumptionreading.domain.repository;

import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link ConsumptionReading}, implementado en {@code infrastructure}.
 */
public interface IConsumptionReadingRepository {

    Optional<ConsumptionReading> findByUuid(UUID uuid);

    Optional<ConsumptionReading> findByAccountReceivableUuid(UUID accountReceivableUuid);

    boolean existsByAccountReceivableUuid(UUID accountReceivableUuid);

    ConsumptionReading insert(ConsumptionReading consumptionReading);
}
