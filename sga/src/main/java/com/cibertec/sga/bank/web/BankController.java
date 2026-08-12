package com.cibertec.sga.bank.web;

import com.cibertec.sga.bank.application.IBankService;
import com.cibertec.sga.bank.domain.error.BankError;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bank.web.dto.BankRequest;
import com.cibertec.sga.bank.web.dto.BankResponse;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
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
 * Endpoints de bancos (RF-12): listar (búsqueda + paginación), obtener, crear, editar y
 * desactivar.
 */
@RestController
@RequestMapping("/api/banks")
@Tag(name = "Bancos", description = "Gestión de bancos y cuentas bancarias")
public class BankController {

    private final IBankService bankService;
    private final BankDtoMapper dtoMapper;

    public BankController(IBankService bankService, BankDtoMapper dtoMapper) {
        this.bankService = bankService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar bancos (búsqueda por nombre/número de cuenta, filtro por activo, paginado)")
    public PagedModel<BankResponse> search(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Boolean active,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(bankService.search(search, active, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener un banco por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Bank, BankError> result = bankService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Crear un banco")
    public ResponseEntity<?> create(@Valid @RequestBody BankRequest request, HttpServletRequest httpRequest) {
        Result<Bank, BankError> result = bankService.create(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Editar un banco")
    public ResponseEntity<?> update(
        @PathVariable UUID uuid, @Valid @RequestBody BankRequest request, HttpServletRequest httpRequest
    ) {
        Result<Bank, BankError> result = bankService.update(uuid, dtoMapper.toCommand(request));
        return ResultResponse.ok(result.map(dtoMapper::toResponse), httpRequest);
    }

    @PatchMapping("/{uuid}/deactivate")
    @PreAuthorize("hasRole('Administrator')")
    @Operation(summary = "Desactivar un banco (soft delete)")
    public ResponseEntity<?> deactivate(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<Bank, BankError> result = bankService.deactivate(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }
}
