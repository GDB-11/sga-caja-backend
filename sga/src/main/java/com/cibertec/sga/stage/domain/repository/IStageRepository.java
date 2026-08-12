package com.cibertec.sga.stage.domain.repository;

import com.cibertec.sga.stage.domain.model.Stage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link Stage}, implementado en {@code infrastructure}.
 */
public interface IStageRepository {

    List<Stage> findAll();

    Optional<Stage> findByUuid(UUID uuid);
}
