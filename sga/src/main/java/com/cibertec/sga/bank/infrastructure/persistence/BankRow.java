package com.cibertec.sga.bank.infrastructure.persistence;

import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Bank"} con la moneda resuelta vía JOIN, para evitar
 * N+1 al listar/obtener bancos.
 */
public interface BankRow {

    UUID getUuid();

    String getName();

    String getAccountNumber();

    String getCci();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();

    Boolean getIsActive();
}
