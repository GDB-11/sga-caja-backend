package com.cibertec.sga.service.domain.repository;

import com.cibertec.sga.service.domain.model.Service;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Service}, implementado en {@code infrastructure}.
 */
public interface IServiceRepository {

    Page<Service> search(String search, Boolean active, Pageable pageable);

    Optional<Service> findByUuid(UUID uuid);

    Service insert(Service service);

    Service update(UUID uuid, Service service);

    Service deactivate(UUID uuid);

    Service activate(UUID uuid);
}
