package com.cibertec.sga.stage.infrastructure.persistence;

import com.cibertec.sga.stage.domain.model.Stage;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link StageEntity} (fila de tabla) y {@link Stage} (modelo de dominio).
 */
@Component
public class StageMapper {

    public Stage toDomain(StageEntity entity) {
        return Stage.builder()
            .uuid(entity.getUuid())
            .code(entity.getCode())
            .name(entity.getName())
            .build();
    }
}
