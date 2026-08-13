package com.cibertec.sga.incomecategory.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.incomecategory.application.IIncomeCategoryService;
import com.cibertec.sga.incomecategory.domain.error.IncomeCategoryError;
import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.incomecategory.web.dto.IncomeCategoryResponse;
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
 * Endpoints de solo lectura para categorías de ingreso (catálogo sembrado por migración).
 */
@RestController
@RequestMapping("/api/income-categories")
@Tag(name = "Categorías de ingreso", description = "Consulta de categorías de ingresos externos")
public class IncomeCategoryController {

    private final IIncomeCategoryService incomeCategoryService;
    private final IncomeCategoryDtoMapper dtoMapper;

    public IncomeCategoryController(IIncomeCategoryService incomeCategoryService, IncomeCategoryDtoMapper dtoMapper) {
        this.incomeCategoryService = incomeCategoryService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar categorías de ingreso")
    public List<IncomeCategoryResponse> findAll() {
        return incomeCategoryService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener una categoría de ingreso por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<IncomeCategory, IncomeCategoryError> result = incomeCategoryService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
