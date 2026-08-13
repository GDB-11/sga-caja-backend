package com.cibertec.sga.accountreceivable.web;

import com.cibertec.sga.accountreceivable.application.IAccountReceivableService;
import com.cibertec.sga.accountreceivable.domain.error.AccountReceivableError;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivable.web.dto.AccountReceivableMovementResponse;
import com.cibertec.sga.accountreceivable.web.dto.AccountReceivableResponse;
import com.cibertec.sga.accountreceivable.web.dto.GenerateByMemberRequest;
import com.cibertec.sga.accountreceivable.web.dto.GenerateByStallRequest;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de cuentas por cobrar (RF-16, RF-18): listar (filtros + paginación), obtener, y
 * generar cuentas por puestos o por socios. Disponible tanto para Administrator como para
 * CashierOperator, según la matriz RBAC del plan (CU-02: "operador autorizado").
 */
@RestController
@RequestMapping("/api/account-receivables")
@Tag(name = "Cuentas por cobrar", description = "Generación y consulta de cuentas por cobrar")
public class AccountReceivableController {

    private final IAccountReceivableService accountReceivableService;
    private final AccountReceivableDtoMapper dtoMapper;

    public AccountReceivableController(IAccountReceivableService accountReceivableService, AccountReceivableDtoMapper dtoMapper) {
        this.accountReceivableService = accountReceivableService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar cuentas por cobrar (filtro por servicio/socio/puesto, paginado)")
    public PagedModel<AccountReceivableResponse> search(
        @RequestParam(required = false) UUID serviceUuid,
        @RequestParam(required = false) UUID memberUuid,
        @RequestParam(required = false) UUID stallUuid,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(accountReceivableService.search(serviceUuid, memberUuid, stallUuid, pageable).map(dtoMapper::toResponse));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener una cuenta por cobrar por Uuid")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<AccountReceivable, AccountReceivableError> result = accountReceivableService.findByUuid(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @PostMapping("/generate-by-stall")
    @Operation(summary = "Generar cuentas por cobrar de un servicio para todos los puestos activos (RF-16)")
    public ResponseEntity<?> generateByStall(@Valid @RequestBody GenerateByStallRequest request, HttpServletRequest httpRequest) {
        Result<List<AccountReceivable>, AccountReceivableError> result =
            accountReceivableService.generateByStall(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(this::toResponseList), httpRequest);
    }

    @PostMapping("/generate-by-member")
    @Operation(summary = "Generar cuentas por cobrar de un servicio para socios activos, filtrable por etapa (RF-18)")
    public ResponseEntity<?> generateByMember(@Valid @RequestBody GenerateByMemberRequest request, HttpServletRequest httpRequest) {
        Result<List<AccountReceivable>, AccountReceivableError> result =
            accountReceivableService.generateByMember(dtoMapper.toCommand(request));
        return ResultResponse.created(result.map(this::toResponseList), httpRequest);
    }

    @PatchMapping("/{uuid}/exempt")
    @PreAuthorize("hasRole('CashierOperator')")
    @Operation(summary = "Marcar una cuenta por cobrar pendiente como exonerada (RF-21)")
    public ResponseEntity<?> markExempt(@PathVariable UUID uuid, HttpServletRequest request) {
        Result<AccountReceivable, AccountReceivableError> result = accountReceivableService.markExempt(uuid);
        return ResultResponse.ok(result.map(dtoMapper::toResponse), request);
    }

    @GetMapping("/summary")
    @Operation(summary = "Resumen de cuentas y movimientos relacionados de un socio o puesto (RF-26)")
    public ResponseEntity<?> summary(
        @RequestParam(required = false) UUID memberUuid, @RequestParam(required = false) UUID stallUuid,
        HttpServletRequest request
    ) {
        Result<List<AccountReceivableMovement>, AccountReceivableError> result =
            accountReceivableService.summary(memberUuid, stallUuid);
        return ResultResponse.ok(result.map(this::toMovementResponseList), request);
    }

    private List<AccountReceivableResponse> toResponseList(List<AccountReceivable> accountReceivables) {
        return accountReceivables.stream().map(dtoMapper::toResponse).toList();
    }

    private List<AccountReceivableMovementResponse> toMovementResponseList(List<AccountReceivableMovement> movements) {
        return movements.stream().map(dtoMapper::toResponse).toList();
    }
}
