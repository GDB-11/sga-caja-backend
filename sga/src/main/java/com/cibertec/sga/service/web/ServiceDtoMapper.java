package com.cibertec.sga.service.web;

import com.cibertec.sga.service.application.ServiceCommand;
import com.cibertec.sga.service.domain.model.Service;
import com.cibertec.sga.service.web.dto.ServiceRequest;
import com.cibertec.sga.service.web.dto.ServiceResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Service} y los DTOs de {@code web}.
 */
@Component
public class ServiceDtoMapper {

    public ServiceResponse toResponse(Service service) {
        return new ServiceResponse(
            service.getUuid(),
            service.getName(),
            new ServiceResponse.RecurrenceTypeRef(service.getRecurrenceType().getUuid(), service.getRecurrenceType().getName()),
            new ServiceResponse.ChargeTargetTypeRef(
                service.getChargeTargetType().getUuid(), service.getChargeTargetType().getName()
            ),
            new ServiceResponse.CurrencyRef(
                service.getCurrency().getUuid(), service.getCurrency().getCode(), service.getCurrency().getName()
            ),
            service.isConsumptionBased(),
            service.getCost(),
            service.getUnitCost(),
            service.isActive()
        );
    }

    public ServiceCommand toCommand(ServiceRequest request) {
        return new ServiceCommand(
            request.name(),
            request.recurrenceTypeUuid(),
            request.chargeTargetTypeUuid(),
            request.currencyUuid(),
            request.consumptionBased(),
            request.cost(),
            request.unitCost()
        );
    }
}
