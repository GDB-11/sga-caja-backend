package com.cibertec.sga.accountreceivable.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "AccountReceivable"} con servicio, socio/puesto y
 * estado resueltos vía JOIN, para evitar N+1 al listar/obtener cuentas por cobrar.
 */
public interface AccountReceivableRow {

    UUID getUuid();

    UUID getServiceUuid();

    String getServiceName();

    UUID getRecurrenceTypeUuid();

    String getRecurrenceTypeName();

    UUID getChargeTargetTypeUuid();

    String getChargeTargetTypeName();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();

    Boolean getServiceIsConsumptionBased();

    BigDecimal getServiceCost();

    BigDecimal getServiceUnitCost();

    Boolean getServiceIsActive();

    UUID getMemberUuid();

    String getMemberFullName();

    UUID getStallUuid();

    String getStallNumber();

    LocalDate getPeriodStartDate();

    LocalDate getPeriodEndDate();

    BigDecimal getAmount();

    UUID getStatusUuid();

    String getStatusName();
}
