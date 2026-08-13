package com.cibertec.sga.service.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.service.domain.error.ServiceError;
import com.cibertec.sga.service.domain.model.Service;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso de {@code Service} (RF-13–RF-15): listar (búsqueda + paginación), obtener,
 * crear, editar y desactivar servicios cobrables. Es la única interfaz que se inyecta en
 * {@code ServiceController}.
 */
public interface IServiceService {

    Page<Service> search(String search, Boolean active, Pageable pageable);

    Result<Service, ServiceError> findByUuid(UUID uuid);

    Result<Service, ServiceError> create(ServiceCommand command);

    Result<Service, ServiceError> update(UUID uuid, ServiceCommand command);

    Result<Service, ServiceError> deactivate(UUID uuid);
}
