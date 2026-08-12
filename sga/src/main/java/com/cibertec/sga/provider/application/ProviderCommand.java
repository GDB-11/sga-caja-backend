package com.cibertec.sga.provider.application;

/**
 * Datos de entrada para crear/editar un proveedor.
 */
public record ProviderCommand(String name, String document) {
}
