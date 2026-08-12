package com.cibertec.sga.bank.web.dto;

import java.util.UUID;

public record BankResponse(
    UUID uuid, String name, String accountNumber, String cci, CurrencyRef currency, boolean active
) {
    public record CurrencyRef(UUID uuid, String code, String name) {
    }
}
