package com.cibertec.sga.bank.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record BankRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String name,

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(max = 50, message = "El número de cuenta no puede superar los 50 caracteres")
    String accountNumber,

    @NotBlank(message = "El CCI es obligatorio")
    @Size(max = 50, message = "El CCI no puede superar los 50 caracteres")
    String cci,

    @NotNull(message = "La moneda es obligatoria")
    UUID currencyUuid
) {
}
