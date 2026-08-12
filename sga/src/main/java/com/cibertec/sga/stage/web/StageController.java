package com.cibertec.sga.stage.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.stage.application.IStageService;
import com.cibertec.sga.stage.domain.error.StageError;
import com.cibertec.sga.stage.domain.model.Stage;
import com.cibertec.sga.stage.web.dto.StageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de solo lectura para etapas de socio (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/stages")
@Tag(name = "Etapas", description = "Consulta de etapas de socio")
public class StageController {

    private final IStageService stageService;
    private final StageDtoMapper dtoMapper;

    public StageController(IStageService stageService, StageDtoMapper dtoMapper) {
        this.stageService = stageService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar etapas de socio")
    public List<StageResponse> findAll() {
        return stageService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener una etapa por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Stage, StageError> result = stageService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
