package com.cibertec.sga.businesstype.web;

import com.cibertec.sga.businesstype.application.IBusinessTypeService;
import com.cibertec.sga.businesstype.domain.error.BusinessTypeError;
import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.businesstype.web.dto.BusinessTypeRequest;
import com.cibertec.sga.businesstype.web.dto.BusinessTypeResponse;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de giros comerciales (RF-08): listar, obtener, crear, editar y eliminar.
 */
@RestController
@RequestMapping("/api/business-types")
@Tag(name = "Giros comerciales", description = "Gestión de giros comerciales (BusinessType)")
public class BusinessTypeController {

    private final IBusinessTypeService businessTypeService;
    private final BusinessTypeDtoMapper dtoMapper;

    public BusinessTypeController(IBusinessTypeService businessTypeService, BusinessTypeDtoMapper dtoMapper) {
        this.businessTypeService = businessTypeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar giros comerciales")
    public List<BusinessTypeResponse> findAll() {
        return businessTypeService.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un giro comercial por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<BusinessType, BusinessTypeError> result = businessTypeService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Crear un giro comercial")
    public ResponseEntity<?> create(@Valid @RequestBody BusinessTypeRequest request, HttpServletRequest httpRequest) {
        Result<BusinessType, BusinessTypeError> result = businessTypeService.create(request.name());
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Editar un giro comercial")
    public ResponseEntity<?> update(
        @PathVariable UUID uuid, @Valid @RequestBody BusinessTypeRequest request, HttpServletRequest httpRequest
    ) {
        Result<BusinessType, BusinessTypeError> result = businessTypeService.update(uuid, request.name());
        return ResultResponse.ok(result.map(dtoMapper::toResponse), httpRequest);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Eliminar un giro comercial")
    public ResponseEntity<?> delete(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Void, BusinessTypeError> result = businessTypeService.delete(uuid);
        if (result.isFailure()) {
            return ResultResponse.ok(result, request);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
