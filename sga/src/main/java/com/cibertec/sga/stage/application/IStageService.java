package com.cibertec.sga.stage.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.stage.domain.error.StageError;
import com.cibertec.sga.stage.domain.model.Stage;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code Stage}: listar y obtener etapas de socio (catálogo de solo lectura).
 * Es la única interfaz que se inyecta en {@code StageController}.
 */
public interface IStageService {

    List<Stage> findAll();

    Result<Stage, StageError> findByUuid(UUID uuid);
}
