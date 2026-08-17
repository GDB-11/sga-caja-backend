package com.cibertec.sga.stall.domain.repository;

import com.cibertec.sga.stall.domain.model.Stall;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de persistencia para {@link Stall}, implementado en {@code infrastructure}.
 */
public interface IStallRepository {

    Page<Stall> search(String search, Boolean active, Pageable pageable);

    Optional<Stall> findByUuid(UUID uuid);

    /**
     * Puestos activos, sin paginar — usado por la generación de cuentas por cobrar (RF-16).
     */
    List<Stall> findAllActive();

    boolean existsByNumber(String number);

    boolean existsByNumberAndUuidNot(String number, UUID uuid);

    Stall insert(Stall stall);

    Stall update(UUID uuid, Stall stall);

    Stall deactivate(UUID uuid);

    Stall activate(UUID uuid);
}
