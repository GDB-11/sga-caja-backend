package com.cibertec.sga.bank.application;

import java.util.UUID;

/**
 * Datos de entrada para crear/editar un banco.
 */
public record BankCommand(String name, String accountNumber, String cci, UUID currencyUuid) {
}
