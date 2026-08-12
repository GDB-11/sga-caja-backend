package com.cibertec.sga.role.domain.repository;

import com.cibertec.sga.role.domain.model.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link Role}. Sin operaciones de escritura: los roles son datos
 * de referencia fijos (sembrados por migración), no gestionados vía API (RF-01-RF-04).
 */
public interface IRoleRepository {

    List<Role> findAll();

    Optional<Role> findByUuid(UUID uuid);
}
