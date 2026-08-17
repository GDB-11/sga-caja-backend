package com.cibertec.sga.provider.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.provider.application.IProviderService;
import com.cibertec.sga.provider.domain.error.ProviderError;
import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.provider.web.dto.ProviderRequest;
import com.cibertec.sga.provider.web.dto.ProviderResponse;
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
 * Endpoints de proveedores: listar (búsqueda + paginación), obtener, crear, editar y
 * desactivar (catálogo inferido de RF-27).
 */
@RestController
@RequestMapping("/api/providers")
@Tag(name = "Proveedores", description = "Gestión de proveedores")
public class ProviderController {

    private final IProviderService providerService;
    private final ProviderDtoMapper dtoMapper;

    public ProviderController(IProviderService providerService, ProviderDtoMapper dtoMapper) {
        this.providerService = providerService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar proveedores (búsqueda por nombre/documento, filtro por activo, paginado)")
    public PagedModel<ProviderResponse> search(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Boolean active,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(providerService.search(search, active, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un proveedor por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Provider, ProviderError> result = providerService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Crear un proveedor")
    public ResponseEntity<?> create(@Valid @RequestBody ProviderRequest request, HttpServletRequest httpRequest) {
        Result<Provider, ProviderError> result = providerService.create(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Editar un proveedor")
    public ResponseEntity<?> update(
        @PathVariable UUID uuid, @Valid @RequestBody ProviderRequest request, HttpServletRequest httpRequest
    ) {
        Result<Provider, ProviderError> result = providerService.update(uuid, dtoMapper.toCommand(request));
        return ResultResponse.ok(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PatchMapping("/{uuid}/deactivate")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Desactivar un proveedor (soft delete)")
    public ResponseEntity<?> deactivate(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Provider, ProviderError> result = providerService.deactivate(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PatchMapping("/{uuid}/activate")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Reactivar un proveedor")
    public ResponseEntity<?> activate(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Provider, ProviderError> result = providerService.activate(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
