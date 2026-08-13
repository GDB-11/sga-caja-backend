package com.cibertec.sga.consumptionreading.infrastructure.persistence;

import com.cibertec.sga.accountreceivable.infrastructure.persistence.AccountReceivableJpaRepository;
import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ConsumptionReadingEntity}/{@link ConsumptionReadingRow} (persistencia) y
 * {@link ConsumptionReading} (modelo de dominio). Resuelve {@code AccountReceivableId} a partir
 * del {@code Uuid} del modelo de dominio vía {@link AccountReceivableJpaRepository} (dependencia
 * infra-a-infra) — el modelo de dominio nunca conoce Ids internos.
 */
@Component
public class ConsumptionReadingMapper {

    private final AccountReceivableJpaRepository accountReceivableJpaRepository;

    public ConsumptionReadingMapper(AccountReceivableJpaRepository accountReceivableJpaRepository) {
        this.accountReceivableJpaRepository = accountReceivableJpaRepository;
    }

    public ConsumptionReading toDomain(ConsumptionReadingRow row) {
        return ConsumptionReading.builder()
            .uuid(row.getUuid())
            .accountReceivableUuid(row.getAccountReceivableUuid())
            .initialReading(row.getInitialReading())
            .finalReading(row.getFinalReading())
            .unitCost(row.getUnitCost())
            .calculatedAmount(row.getCalculatedAmount())
            .build();
    }

    public ConsumptionReadingEntity toNewEntity(ConsumptionReading consumptionReading) {
        return ConsumptionReadingEntity.builder()
            .accountReceivableId(resolveAccountReceivableId(consumptionReading))
            .initialReading(consumptionReading.getInitialReading())
            .finalReading(consumptionReading.getFinalReading())
            .unitCost(consumptionReading.getUnitCost())
            .build();
    }

    private Long resolveAccountReceivableId(ConsumptionReading consumptionReading) {
        return accountReceivableJpaRepository.findEntityByUuid(consumptionReading.getAccountReceivableUuid()).orElseThrow().getId();
    }
}
