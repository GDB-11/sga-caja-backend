package com.cibertec.sga.bankexchange.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "BankExchange"} con banco y comprobante resueltos vía
 * JOIN. La cuenta por cobrar y el banco completos se resuelven aparte, vía sus respectivos
 * repositorios de dominio, para reutilizar sus reglas de mapeo (evita duplicar el join amplio
 * de {@code AccountReceivable}).
 */
public interface BankExchangeRow {

    UUID getUuid();

    UUID getAccountReceivableUuid();

    UUID getBankUuid();

    UUID getReceiptUuid();

    Long getReceiptCorrelativeNumber();

    LocalDate getReceiptIssueDate();

    BigDecimal getReceiptAmount();

    String getReceiptDescription();

    UUID getReceiptTypeUuid();

    String getReceiptTypeName();

    LocalDate getDepositDate();

    BigDecimal getAmount();
}
