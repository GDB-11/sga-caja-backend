package com.cibertec.sga.stall.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.stall.application.IStallService;
import com.cibertec.sga.stall.domain.error.StallError;
import com.cibertec.sga.stall.domain.model.Stall;
import com.cibertec.sga.stall.web.dto.StallRequest;
import com.cibertec.sga.stall.web.dto.StallResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de puestos (RF-09–RF-11): listar (búsqueda + paginación), obtener, crear, editar y
 * desactivar.
 */
@RestController
@RequestMapping("/api/stalls")
@Tag(name = "Puestos", description = "Gestión de puestos")
public class StallController {

    private final IStallService stallService;
    private final StallDtoMapper dtoMapper;

    public StallController(IStallService stallService, StallDtoMapper dtoMapper) {
        this.stallService = stallService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar puestos (búsqueda por número/inquilino, filtro por activo, paginado)")
    public PagedModel<StallResponse> search(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Boolean active,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(stallService.search(search, active, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un puesto por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Stall, StallError> result = stallService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Crear un puesto")
    public ResponseEntity<?> create(@Valid @RequestBody StallRequest request, HttpServletRequest httpRequest) {
        Result<Stall, StallError> result = stallService.create(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Editar un puesto")
    public ResponseEntity<?> update(
        @PathVariable UUID uuid, @Valid @RequestBody StallRequest request, HttpServletRequest httpRequest
    ) {
        Result<Stall, StallError> result = stallService.update(uuid, dtoMapper.toCommand(request));
        return ResultResponse.ok(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PatchMapping("/{uuid}/deactivate")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Desactivar un puesto (soft delete)")
    public ResponseEntity<?> deactivate(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Stall, StallError> result = stallService.deactivate(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
