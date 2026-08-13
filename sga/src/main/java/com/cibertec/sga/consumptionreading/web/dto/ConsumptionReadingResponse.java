package com.cibertec.sga.consumptionreading.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ConsumptionReadingResponse(
    UUID uuid,
    UUID accountReceivableUuid,
    BigDecimal initialReading,
    BigDecimal finalReading,
    BigDecimal unitCost,
    BigDecimal calculatedAmount
) {
}
