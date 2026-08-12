package com.cibertec.sga.member.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record MemberRequest(
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    String code,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String firstName,

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    String lastName,

    @Size(max = 20, message = "La acción no puede superar los 20 caracteres")
    String shareNumber,

    @NotNull(message = "La etapa es obligatoria")
    UUID stageUuid,

    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    LocalDate birthDate
) {
}
