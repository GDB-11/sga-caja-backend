package com.cibertec.sga.provider.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.provider.domain.error.ProviderError;
import com.cibertec.sga.provider.domain.model.Provider;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Provider}: listar (búsqueda + paginación), obtener, crear, editar y
 * desactivar proveedores. Es la única interfaz que se inyecta en {@code ProviderController}.
 */
public interface IProviderService {

    Page<Provider> search(String search, Boolean active, Pageable pageable);

    Result<Provider, ProviderError> findByUuid(UUID uuid);

    Result<Provider, ProviderError> create(ProviderCommand command);

    Result<Provider, ProviderError> update(UUID uuid, ProviderCommand command);

    Result<Provider, ProviderError> deactivate(UUID uuid);

    Result<Provider, ProviderError> activate(UUID uuid);
}
