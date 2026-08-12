package com.cibertec.sga.recurrencetype.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.recurrencetype.application.IRecurrenceTypeService;
import com.cibertec.sga.recurrencetype.domain.error.RecurrenceTypeError;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import com.cibertec.sga.recurrencetype.web.dto.RecurrenceTypeResponse;
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
 * Endpoints de solo lectura para tipos de recurrencia (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/recurrence-types")
@Tag(name = "Tipos de recurrencia", description = "Consulta de tipos de recurrencia de servicios")
public class RecurrenceTypeController {

    private final IRecurrenceTypeService recurrenceTypeService;
    private final RecurrenceTypeDtoMapper dtoMapper;

    public RecurrenceTypeController(IRecurrenceTypeService recurrenceTypeService, RecurrenceTypeDtoMapper dtoMapper) {
        this.recurrenceTypeService = recurrenceTypeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar tipos de recurrencia")
    public List<RecurrenceTypeResponse> findAll() {
        return recurrenceTypeService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un tipo de recurrencia por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<RecurrenceType, RecurrenceTypeError> result = recurrenceTypeService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
