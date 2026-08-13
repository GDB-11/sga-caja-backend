package com.cibertec.sga.consumptionreading.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.consumptionreading.domain.error.ConsumptionReadingError;
import com.cibertec.sga.consumptionreading.domain.model.ConsumptionReading;
import java.util.UUID;

/**
 * Casos de uso de {@code ConsumptionReading} (RF-17): registrar la lectura de un servicio por
 * consumo y consultarla. Es la única interfaz que se inyecta en
 * {@code ConsumptionReadingController}.
 */
public interface IConsumptionReadingService {

    Result<ConsumptionReading, ConsumptionReadingError> register(RegisterConsumptionReadingCommand command);

    Result<ConsumptionReading, ConsumptionReadingError> findByUuid(UUID uuid);

    Result<ConsumptionReading, ConsumptionReadingError> findByAccountReceivableUuid(UUID accountReceivableUuid);
}
