package com.cibertec.sga.service.infrastructure.persistence;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.chargetargettype.infrastructure.persistence.ChargeTargetTypeJpaRepository;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.infrastructure.persistence.CurrencyJpaRepository;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.recurrencetype.infrastructure.persistence.RecurrenceTypeJpaRepository;
import com.cibertec.sga.service.domain.model.Service;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ServiceEntity}/{@link ServiceRow} (persistencia) y {@link Service}
 * (modelo de dominio). Resuelve {@code RecurrenceTypeId}/{@code ChargeTargetTypeId}/
 * {@code CurrencyId} a partir de los {@code Uuid} del modelo de dominio vía los
 * {@code JpaRepository} de esos módulos (dependencia infra-a-infra) — el modelo de dominio nunca
 * conoce Ids internos.
 */
@Component
public class ServiceMapper {

    private final RecurrenceTypeJpaRepository recurrenceTypeJpaRepository;
    private final ChargeTargetTypeJpaRepository chargeTargetTypeJpaRepository;
    private final CurrencyJpaRepository currencyJpaRepository;

    public ServiceMapper(
        RecurrenceTypeJpaRepository recurrenceTypeJpaRepository,
        ChargeTargetTypeJpaRepository chargeTargetTypeJpaRepository,
        CurrencyJpaRepository currencyJpaRepository
    ) {
        this.recurrenceTypeJpaRepository = recurrenceTypeJpaRepository;
        this.chargeTargetTypeJpaRepository = chargeTargetTypeJpaRepository;
        this.currencyJpaRepository = currencyJpaRepository;
    }

    public Service toDomain(ServiceRow row) {
        RecurrenceType recurrenceType = RecurrenceType.builder()
            .uuid(row.getRecurrenceTypeUuid())
            .name(row.getRecurrenceTypeName())
            .build();
        ChargeTargetType chargeTargetType = ChargeTargetType.builder()
            .uuid(row.getChargeTargetTypeUuid())
            .name(row.getChargeTargetTypeName())
            .build();
        Currency currency = Currency.builder()
            .uuid(row.getCurrencyUuid())
            .code(row.getCurrencyCode())
            .name(row.getCurrencyName())
            .build();
        return Service.builder()
            .uuid(row.getUuid())
            .name(row.getName())
            .recurrenceType(recurrenceType)
            .chargeTargetType(chargeTargetType)
            .currency(currency)
            .consumptionBased(row.getIsConsumptionBased())
            .cost(row.getCost())
            .unitCost(row.getUnitCost())
            .active(row.getIsActive())
            .build();
    }

    public ServiceEntity toNewEntity(Service service) {
        return ServiceEntity.builder()
            .name(service.getName())
            .recurrenceTypeId(resolveRecurrenceTypeId(service))
            .chargeTargetTypeId(resolveChargeTargetTypeId(service))
            .currencyId(resolveCurrencyId(service))
            .consumptionBased(service.isConsumptionBased())
            .cost(service.getCost())
            .unitCost(service.getUnitCost())
            .build();
    }

    public void updateEntity(ServiceEntity entity, Service service) {
        entity.setName(service.getName());
        entity.setRecurrenceTypeId(resolveRecurrenceTypeId(service));
        entity.setChargeTargetTypeId(resolveChargeTargetTypeId(service));
        entity.setCurrencyId(resolveCurrencyId(service));
        entity.setConsumptionBased(service.isConsumptionBased());
        entity.setCost(service.getCost());
        entity.setUnitCost(service.getUnitCost());
    }

    private Long resolveRecurrenceTypeId(Service service) {
        return recurrenceTypeJpaRepository.findByUuid(service.getRecurrenceType().getUuid()).orElseThrow().getId();
    }

    private Long resolveChargeTargetTypeId(Service service) {
        return chargeTargetTypeJpaRepository.findByUuid(service.getChargeTargetType().getUuid()).orElseThrow().getId();
    }

    private Long resolveCurrencyId(Service service) {
        return currencyJpaRepository.findByUuid(service.getCurrency().getUuid()).orElseThrow().getId();
    }
}
