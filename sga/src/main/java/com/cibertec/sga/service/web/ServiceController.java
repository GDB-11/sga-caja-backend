package com.cibertec.sga.service.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.service.application.IServiceService;
import com.cibertec.sga.service.domain.error.ServiceError;
import com.cibertec.sga.service.domain.model.Service;
import com.cibertec.sga.service.web.dto.ServiceRequest;
import com.cibertec.sga.service.web.dto.ServiceResponse;
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
 * Endpoints de servicios cobrables (RF-13–RF-15): listar (búsqueda + paginación), obtener,
 * crear, editar y desactivar.
 */
@RestController
@RequestMapping("/api/services")
@Tag(name = "Servicios", description = "Gestión de servicios cobrables")
public class ServiceController {

    private final IServiceService serviceService;
    private final ServiceDtoMapper dtoMapper;

    public ServiceController(IServiceService serviceService, ServiceDtoMapper dtoMapper) {
        this.serviceService = serviceService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar servicios (búsqueda por nombre, filtro por activo, paginado)")
    public PagedModel<ServiceResponse> search(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Boolean active,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(serviceService.search(search, active, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un servicio por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Service, ServiceError> result = serviceService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Crear un servicio")
    public ResponseEntity<?> create(@Valid @RequestBody ServiceRequest request, HttpServletRequest httpRequest) {
        Result<Service, ServiceError> result = serviceService.create(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Editar un servicio")
    public ResponseEntity<?> update(
        @PathVariable UUID uuid, @Valid @RequestBody ServiceRequest request, HttpServletRequest httpRequest
    ) {
        Result<Service, ServiceError> result = serviceService.update(uuid, dtoMapper.toCommand(request));
        return ResultResponse.ok(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PatchMapping("/{uuid}/deactivate")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Desactivar un servicio (soft delete)")
    public ResponseEntity<?> deactivate(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Service, ServiceError> result = serviceService.deactivate(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
