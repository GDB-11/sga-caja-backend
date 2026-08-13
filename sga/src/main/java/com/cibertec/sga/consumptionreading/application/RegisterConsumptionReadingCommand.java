package com.cibertec.sga.consumptionreading.application;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos de entrada para registrar la lectura de un servicio por consumo (RF-17). El costo
 * unitario no se recibe del usuario: se toma del {@code Service} de la cuenta por cobrar en el
 * momento del registro, quedando fijo en la lectura (RN-05).
 */
public record RegisterConsumptionReadingCommand(UUID accountReceivableUuid, BigDecimal initialReading, BigDecimal finalReading) {
}
