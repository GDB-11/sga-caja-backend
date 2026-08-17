package com.cibertec.sga.provider.domain.repository;

import com.cibertec.sga.provider.domain.model.Provider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Provider}, implementado en {@code infrastructure}.
 */
public interface IProviderRepository {

    Page<Provider> search(String search, Boolean active, Pageable pageable);

    Optional<Provider> findByUuid(UUID uuid);

    /**
     * Usado por la carga masiva de egresos (RF-28) para resolver el proveedor de cada fila por
     * nombre exacto — el archivo de carga no trae Uuids.
     */
    Optional<Provider> findByName(String name);

    Provider insert(Provider provider);

    Provider update(UUID uuid, Provider provider);

    Provider deactivate(UUID uuid);

    Provider activate(UUID uuid);
}
