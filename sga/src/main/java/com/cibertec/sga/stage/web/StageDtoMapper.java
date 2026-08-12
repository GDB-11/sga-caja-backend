package com.cibertec.sga.stage.web;

import com.cibertec.sga.stage.domain.model.Stage;
import com.cibertec.sga.stage.web.dto.StageResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Stage} y los DTOs de {@code web}.
 */
@Component
public class StageDtoMapper {

    public StageResponse toResponse(Stage stage) {
        return new StageResponse(stage.getUuid(), stage.getCode(), stage.getName());
    }
}
