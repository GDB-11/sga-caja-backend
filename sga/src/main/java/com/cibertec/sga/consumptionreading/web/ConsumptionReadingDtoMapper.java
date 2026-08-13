package com.cibertec.sga.consumptionreading.web;

import com.cibertec.sga.consumptionreading.application.RegisterConsumptionReadingCommand;
import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import com.cibertec.sga.consumptionreading.web.dto.ConsumptionReadingResponse;
import com.cibertec.sga.consumptionreading.web.dto.RegisterConsumptionReadingRequest;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link ConsumptionReading} y los DTOs de {@code web}.
 */
@Component
public class ConsumptionReadingDtoMapper {

    public ConsumptionReadingResponse toResponse(ConsumptionReading consumptionReading) {
        return new ConsumptionReadingResponse(
            consumptionReading.getUuid(),
            consumptionReading.getAccountReceivableUuid(),
            consumptionReading.getInitialReading(),
            consumptionReading.getFinalReading(),
            consumptionReading.getUnitCost(),
            consumptionReading.getCalculatedAmount()
        );
    }

    public RegisterConsumptionReadingCommand toCommand(RegisterConsumptionReadingRequest request) {
        return new RegisterConsumptionReadingCommand(
            request.accountReceivableUuid(), request.initialReading(), request.finalReading()
        );
    }
}
