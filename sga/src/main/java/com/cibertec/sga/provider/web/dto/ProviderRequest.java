package com.cibertec.sga.provider.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProviderRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    String name,

    @Size(max = 20, message = "El documento no puede superar los 20 caracteres")
    String document
) {
}
