package com.cibertec.sga.common.security;

import java.util.UUID;

/**
 * Principal autenticado colocado en el {@code SecurityContext} por {@code JwtAuthenticationFilter}
 * tras resolver el usuario en base de datos por su {@code Uuid} (nunca se confía solo en el JWT
 * para rol/estado activo — RNF-02). Lleva el {@code Id} interno para {@code AuditorAware}, que
 * es la única razón por la que este tipo, y no el modelo de dominio {@code User}, cruza a la capa
 * de seguridad.
 */
public record AuthenticatedUser(Long id, UUID uuid, String username, String roleName) {
}
