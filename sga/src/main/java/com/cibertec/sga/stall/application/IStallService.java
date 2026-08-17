package com.cibertec.sga.stall.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.stall.domain.error.StallError;
import com.cibertec.sga.stall.domain.model.Stall;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Stall} (RF-09–RF-11): listar (búsqueda + paginación), obtener, crear,
 * editar y desactivar puestos. Es la única interfaz que se inyecta en {@code StallController}.
 */
public interface IStallService {

    Page<Stall> search(String search, Boolean active, Pageable pageable);

    Result<Stall, StallError> findByUuid(UUID uuid);

    Result<Stall, StallError> create(StallCommand command);

    Result<Stall, StallError> update(UUID uuid, StallCommand command);

    Result<Stall, StallError> deactivate(UUID uuid);

    Result<Stall, StallError> activate(UUID uuid);
}
